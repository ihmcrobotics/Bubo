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

import georegression.geometry.GeometryMath_F64;
import georegression.metric.ClosestPoint3D_F64;
import georegression.metric.Distance3D_F64;
import georegression.struct.line.LineParametric3D_F64;
import georegression.struct.shapes.Cylinder3D_F64;
import org.ddogleg.fitting.modelset.ModelGenerator;

import java.util.List;

/**
 * Cylinder estimation for use in {@link PointCloudShapeDetectionSchnabel2007}.  The sphere is estimated
 * using two points and their normal vectors.  A point in the sphere's axis is found by finding the closest
 * point between the lines defined by the two points + vectors.  The axis is found by taking the cross
 * product of the two normals.  Radius is distance from cylinder's axis.  An extra point is used to validate
 * the found cylinder.
 *
 * @author Peter Abeles
 */
public class GenerateCylinderPointVector implements ModelGenerator<Cylinder3D_F64,PointVectorNN> {

	// tolerance cos(angle) for vector normals
	private double tolAngleCosine;
	// tolerance for each point from the sphere
	private double tolDistance;

	// line defined by two lines.  used to find sphere center
	private LineParametric3D_F64 lineA = new LineParametric3D_F64(false);
	private LineParametric3D_F64 lineB = new LineParametric3D_F64(false);

	public GenerateCylinderPointVector(double tolAngle, double tolDistance) {
		this.tolAngleCosine = Math.cos(Math.PI/2.0-tolAngle);
		this.tolDistance = tolDistance;
	}

	@Override
	public boolean generate(List<PointVectorNN> dataSet, Cylinder3D_F64 output) {

		PointVectorNN pa = dataSet.get(0);
		PointVectorNN pb = dataSet.get(1);
		PointVectorNN pc = dataSet.get(2);

		lineA.p = pa.p;
		lineA.slope = pa.normal;

		lineB.p = pb.p;
		lineB.slope = pb.normal;

		// With perfect data, the closest point between the two lines defined by a point and its normal
		// will lie on the axis of the cylinder
		ClosestPoint3D_F64.closestPoint(lineA,lineB,output.line.p);
		// The cylinder's axis will be along the cross product of the two normal vectors
		GeometryMath_F64.cross(pa.normal,pb.normal,output.line.slope);

		// take the normal of the slope so that it can detect if it has a value of zero, which happens if the
		// two input vectors are the same
		double n = output.line.slope.norm();
		// n should actually always be one since the point normals are normalized to one.
		if( n < 1e-8 )
			return false;
		output.line.slope.x /= n;
		output.line.slope.y /= n;
		output.line.slope.z /= n;

		// set the radius to be the average distance point is from the cylinder's axis
		double ra = Distance3D_F64.distance(output.line,pa.p);
		double rb = Distance3D_F64.distance(output.line,pb.p);
		double rc = Distance3D_F64.distance(output.line,pc.p);

		output.radius = (ra+rb)/2.0;

		// sanity check the model
		return checkModel(output, pc, ra, rb, rc);
	}

	protected final boolean checkModel(Cylinder3D_F64 output, PointVectorNN pc, double ra, double rb, double rc) {
		// check the solution
		if( Math.abs(ra-output.radius) > tolDistance )
			return false;
		if( Math.abs(rb-output.radius) > tolDistance )
			return false;
		if( Math.abs(rc-output.radius) > tolDistance )
			return false;

		// only need to check to see if one angle is off since the other two are within tolerance by definition
		double cosAngle = output.line.slope.dot(pc.normal);
		if(Math.abs(cosAngle) > tolAngleCosine )
			return false;

		return true;
	}

	@Override
	public int getMinimumPoints() {
		return 3;
	}
}