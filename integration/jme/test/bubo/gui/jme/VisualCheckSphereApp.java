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

package bubo.gui.jme;

import bubo.gui.UtilDisplayBubo;
import bubo.gui.d3.PointCloudPanel;
import georegression.geometry.RotationMatrixGenerator;
import georegression.struct.se.Se3_F64;

import java.awt.*;

/**
 * @author Peter Abeles
 */
public class VisualCheckSphereApp {
	public static void main(String[] args) {

		JmeFactoryVisualization3D factory = new JmeFactoryVisualization3D();
		PointCloudPanel panel = factory.displayPointCloud();
		panel.setPreferredSize(new Dimension(640,480));

		UtilDisplayBubo.createWindow(panel, panel, "Sphere");

		// tilt it towards the camera
		Se3_F64 transform = new Se3_F64();
		transform.R = RotationMatrixGenerator.eulerXYZ(0.2,0,0,null);

		Se3_F64 transform2 = new Se3_F64();
		transform2.getT().set(0,0,-2);

		panel.addSphere(0,0,0,0.2,0xFFFF0000);
		panel.addSphere(0,-0.3,1,0.3,0x5000FF00);
	}
}
