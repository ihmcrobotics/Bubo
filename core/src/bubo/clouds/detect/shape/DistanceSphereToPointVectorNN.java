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

package bubo.clouds.detect.shape;

import bubo.clouds.detect.alg.PointVectorNN;
import georegression.metric.Distance3D_F64;
import georegression.struct.point.Vector3D_F64;
import georegression.struct.shapes.Sphere3D_F64;
import org.ddogleg.fitting.modelset.DistanceFromModel;

import java.util.List;

/**
 * Euclidean distance from a {@link georegression.struct.plane.PlaneGeneral3D_F64} for use with {@link bubo.clouds.detect.alg.PointCloudShapeDetectionSchnabel2007}.
 * <p/>
 * todo comment
 *
 * @author Peter Abeles
 */
public class DistanceSphereToPointVectorNN implements DistanceFromModel<Sphere3D_F64, PointVectorNN> {

	Sphere3D_F64 model;
	// tolerance cos(angle) for vector normals
	private double tolAngleCosine;
	// storage for vector from center to a point
	private Vector3D_F64 n = new Vector3D_F64();

	public DistanceSphereToPointVectorNN(double tolAngle) {
		this.tolAngleCosine = Math.cos(tolAngle);
	}

	@Override
	public void setModel(Sphere3D_F64 model) {
		this.model = model;
	}

	@Override
	public double computeDistance(PointVectorNN pv) {
		n.set(pv.p.x - model.center.x, pv.p.y - model.center.y, pv.p.z - model.center.z);
		n.normalize();

		if (Math.abs(n.dot(pv.normal)) < tolAngleCosine)
			return Double.MAX_VALUE;

		return Math.abs(Distance3D_F64.distance(model, pv.p));
	}

	@Override
	public void computeDistance(List<PointVectorNN> points, double[] distance) {
		for (int i = 0; i < points.size(); i++) {
			distance[i] = computeDistance(points.get(i));
		}
	}
}
