/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package joshua.decoder.hypergraph;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import joshua.corpus.Vocabulary;
import joshua.decoder.chart_parser.ComputeNodeResult;
import joshua.decoder.ff.FeatureFunction;
import joshua.decoder.ff.FeatureVector;
import joshua.decoder.hypergraph.ForestWalker.TRAVERSAL;
import joshua.decoder.segment_file.Sentence;

/**
 * this class implement (1) HyperGraph-related data structures (Item and Hyper-edges)
 * 
 * Note: to seed the kbest extraction, each deduction should have the best_cost properly set. We do
 * not require any list being sorted
 * 
 * @author Zhifei Li, <zhifei.work@gmail.com>
 */
public class HyperGraph {

  // pointer to goal HGNode
  public HGNode goalNode = null;

  public int numNodes = -1;
  public int numEdges = -1;
  public Sentence sentence = null;

  static final Logger logger = Logger.getLogger(HyperGraph.class.getName());

  public HyperGraph(HGNode goalNode, int numNodes, int numEdges, Sentence sentence) {
    this.goalNode = goalNode;
    this.numNodes = numNodes;
    this.numEdges = numEdges;
    this.sentence = sentence;
  }
  
  public void count() {
    new ForestWalker().walk(this.goalNode, new HyperGraphCounter(this));
  }
  
  public int sentID() {
    return sentence.id();
  }
  
  public int sentLen() {
    return sentence.length();
  }
  
  private class HyperGraphCounter implements WalkerFunction {

    private HyperGraph hg = null;
    private HashSet<HGNode> nodesVisited = null;
    
    public HyperGraphCounter(HyperGraph hg) {
      this.hg = hg;
      this.hg.numNodes = 0;
      this.hg.numEdges = 0;
      this.nodesVisited = new HashSet<HGNode>();
    }
    
    @Override
    public void apply(HGNode node, int index) {
      if (! nodesVisited.contains(node)) {
        if (node.bestHyperedge.getRule() != null) {
          hg.numNodes++;
          if (node.hyperedges != null)
            hg.numEdges += node.hyperedges.size();
        }
      }
    }
  }

  private class HyperGraphDumper implements WalkerFunction {

    private int node_number = 1;
    private List<FeatureFunction> model = null;
    private PrintWriter out = null;
    
    private HashMap<HGNode, Integer> nodeMap;
    
    public HyperGraphDumper(PrintWriter out, List<FeatureFunction> model) {
      this.out = out;
      this.model = model;
      this.nodeMap = new HashMap<HGNode, Integer>();
    }
    
    @Override
    public void apply(HGNode node, int index) {
      if (! nodeMap.containsKey(node)) { // Make sure each node is listed only once
        nodeMap.put(node,  this.node_number);

        if (node.hyperedges.size() != 0 && node.bestHyperedge.getRule() != null) {
          out.println(this.node_number);
          for (HyperEdge e: node.hyperedges) {
            if (e.getRule() != null) {
              for (int id: e.getRule().getEnglish()) {
                if (id < 0) {
                  out.print(String.format("[%d] ", nodeMap.get(e.getTailNodes().get(-id-1))));
                } else {
                  out.print(String.format("%s ", Vocabulary.word(id)));
                }
              }

              FeatureVector edgeFeatures = ComputeNodeResult.computeTransitionFeatures(
                  model, e, node.i, node.j, sentence);
              out.println(String.format("||| %s", edgeFeatures));
            }
          }
        }
        
        this.node_number++;
      }
    }
  }
  
  /**
   * Dump the hypergraph to the specified file.
   * 
   * @param fileName
   */
  public void dump(String fileName, List<FeatureFunction> model) {
    try ( PrintWriter out = new PrintWriter(fileName, "UTF-8") ) {
      count();
      out.println("# target ||| features");
      out.println(String.format("%d %d", numNodes, numEdges));
      new ForestWalker(TRAVERSAL.POSTORDER).walk(this.goalNode, new HyperGraphDumper(out, model));
    } catch (IOException e) {
      System.err.println("* Can't dump hypergraph to file '" + fileName + "'");
      e.printStackTrace();
    }
  }

  public float bestScore() {
    return this.goalNode.bestHyperedge.getBestDerivationScore();
  }
}
