/*
 * Copyright (c) 2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Project BUBO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bubo.ptcloud.alg;

import org.ddogleg.fitting.modelset.DistanceFromModel;

import java.util.List;
import java.util.Stack;

/**
 * Given an initial set of points which match the model, find all points which match the model by traversing
 * the nearest neighbor graph.  Points are only considered as potential matches if they are directly
 * connected to a node which has already been matched to the model.
 *
 * It knows if a point has been searched because it writes the marker value to the points marker
 *
 * @author Peter Abeles
 */
public class FindMatchSetPointVectorNN<Model> {

	// Computes the distance of a point from the specified model
	private DistanceFromModel<Model,PointVectorNN> modelDistance;

	// list of points which need to be searched
	private Stack<PointVectorNN> open = new Stack<PointVectorNN>();

	// used indicates if a points has been searched
	protected int marker;

	/**
	 * Resets the class into its initial state
	 */
	public void reset() {
		marker = -1;
	}

	/**
	 * Finds the match set by searching the nearest-neighbor graph of the initialSample set.  Points
	 * are marked with a unique ID for this function call so that it knows which ones it has examined.
	 *
	 * @param initialMatch (Input) Set of points which is known to match the given model parameters.
	 * @param param (Input) Model parameters which describe the shape.
	 * @param threshold (Input) Distance threshold for determining inliers.
	 * @param outputMatch (Output) the found set of points which match the shape.
	 */
	protected void selectMatchSet( List<PointVectorNN> initialMatch , Model param , double threshold,
								   List<PointVectorNN> outputMatch ) {
		// initialize data structures
		if( !open.isEmpty())
			throw new IllegalArgumentException("BUG!");
		modelDistance.setModel(param);
		marker++;

		// use the initial set of samples as the seed
		for( int i = 0; i < initialMatch.size(); i++ ) {
			PointVectorNN nn = initialMatch.get(i);
			nn.matchMarker = marker;
			open.add(nn);
		}

		// examine each point until all neighbors which match the model have been found
		while( !open.isEmpty() )  {
			PointVectorNN n = open.pop();
			outputMatch.add(n);

			for( int i = 0; i < n.neighbors.size(); i++ ) {
				PointVectorNN nn = n.neighbors.get(i);

				// see if it has been traversed already
				if( nn.matchMarker != marker ) {

					// see if it's in the inlier set
					double distance = modelDistance.computeDistance(nn);
					if (distance <= threshold) {
						open.add(nn);
					}
					nn.matchMarker = marker;
				}
			}
		}
	}

	/**
	 * Specifies which model is used to compute the distance a point is from the model
	 *
	 * @param modelDistance
	 */
	public void setModelDistance(DistanceFromModel<Model, PointVectorNN> modelDistance) {
		this.modelDistance = modelDistance;
	}
}
