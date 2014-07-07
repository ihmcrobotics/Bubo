/*
 * Copyright (c) 2013-2014, Peter Abeles. All Rights Reserved.
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

package bubo.construct;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class OctreeOps {

	/**
	 * Finds all nodes in the list which are the smallest possible size, e.g. 1x1x1.
	 */
	public static List<Octree_I32> findAllSmallest( List<Octree_I32> allNodes, List<Octree_I32> output ) {

		if( output == null ) {
			output = new ArrayList<Octree_I32>();
		}

		for (int i = 0; i < allNodes.size(); i++) {
			Octree_I32 n = allNodes.get(i);
			if( n.isSmallest() ) {
				output.add(n);
			}
		}

		return output;
	}
}
