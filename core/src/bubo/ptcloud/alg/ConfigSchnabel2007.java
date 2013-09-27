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

import bubo.ptcloud.wrapper.PlaneGeneralSvd_to_ModelFitter;
import georegression.fitting.cylinder.CodecCylinder3D_F64;
import georegression.fitting.cylinder.FitCylinderToPoints_F64;
import georegression.fitting.plane.CodecPlaneGeneral3D_F64;
import georegression.fitting.sphere.CodecSphere3D_F64;
import georegression.fitting.sphere.FitSphereToPoints_F64;

import java.util.ArrayList;
import java.util.List;

/**
 * Tuning parameters for {@link PointCloudShapeDetectionSchnabel2007}
 *
 * @author Peter Abeles
 */
public class ConfigSchnabel2007 {
	/**
	 * description of the shapes it will search for
	 */
	public List<ShapeDescription> models;
	/**
	 * Maximum number of iterations it will perform while refining a shape after detection by RANSAC.
	 */
	public int localFitMaxIterations = 100;
	/**
	 * Exit criteria for shape refinement.  When the model parameters change less than this it exits.
	 */
	public double localFitChangeThreshold = 1e-8;
	/**
	 * Children will be created in an Octree node when the number of points contained inside exceed this number.
	 * Optimal value is data dependent.  Try 50.
	 */
	public int octreeSplit = 100;
	/**
	 * THe minimum number of points that are in the inlier set for a shape for the shape to be accepted.
	 */
	public int minModelAccept = 50;
	/**
	 * RANSAC will be extended by this many iterations each time a new best fit model is found.
	 */
	public int ransacExtension = 15;
	/**
	 * The total maximum number of allowed iterations to find a shape.  This is used to limit the time
	 * spent searching for shapes.  Try 1000
	 */
	public int maximumAllowedIterations = 1000;
	/**
	 * Random seed used in various places, such as RANSAC
	 */
	public long randomSeed = 0xDEADBEEF;

	/**
	 * Checks to see if the specified parameters are internally consistent
	 */
	public void checkConfig() {
		if( octreeSplit < minModelAccept) {
			throw new IllegalArgumentException("octreeSplit should be at least 3 times the ransac sample size, which "+
					"is "+ minModelAccept);
		}
	}

	/**
	 * Creates a default set of parameters which can detect
	 *
	 * @param fitIterations Number of iterations when refining a shape
	 * @param angleTolerance Tolerance in radians to reject a model from an initial sample set
	 * @param distanceTolerance Tolerance in distance used to reject model from an initial sample set
	 * @param ransacDistanceThreshold Euclidean distance that RANSAC considers a point an inlier
	 *
	 * @return ConfigSchnabel2007
	 */
	public static ConfigSchnabel2007 createDefault( int fitIterations ,
													double angleTolerance ,
													double distanceTolerance ,
													double ransacDistanceThreshold )
	{
		List<ShapeDescription> objects = new ArrayList<ShapeDescription>();

		ShapeDescription sphere = new ShapeDescription();
		sphere.modelDistance = new DistanceFromModel_P_to_PVNN(new DistanceSphereToPoint3D());
		sphere.modelGenerator = new GenerateSpherePointVector(angleTolerance,distanceTolerance);
		sphere.modelFitter = new ModelFitter_P_to_PVNN(new FitSphereToPoints_F64(fitIterations));
		sphere.codec = new CodecSphere3D_F64();
		sphere.thresholdFit = ransacDistanceThreshold;

		ShapeDescription plane = new ShapeDescription();
		plane.modelDistance = new DistanceFromModel_P_to_PVNN(new DistancePlaneToPoint3D());
		plane.modelGenerator = new GeneratePlanePointVector(angleTolerance);
		plane.modelFitter = new ModelFitter_P_to_PVNN(new PlaneGeneralSvd_to_ModelFitter());
		plane.codec = new CodecPlaneGeneral3D_F64();
		plane.thresholdFit = ransacDistanceThreshold;

		ShapeDescription cylinder = new ShapeDescription();
		cylinder.modelDistance = new DistanceFromModel_P_to_PVNN(new DistanceCylinderToPoint3D());
		cylinder.modelGenerator = new GenerateCylinderPointVector(angleTolerance,distanceTolerance);
		cylinder.modelFitter = new ModelFitter_P_to_PVNN(new FitCylinderToPoints_F64(fitIterations));
		cylinder.codec = new CodecCylinder3D_F64();
		cylinder.thresholdFit = ransacDistanceThreshold;

		objects.add(sphere);
		objects.add(plane);
		objects.add(cylinder);

		ConfigSchnabel2007 config = new ConfigSchnabel2007();
		config.models = objects;

		return config;
	}
}
