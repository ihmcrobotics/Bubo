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

package bubo.gui.d3;

import bubo.gui.jme.JmeBridgeToAwt;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import georegression.struct.point.Point3D_F64;
import georegression.struct.se.Se3_F64;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Peter Abeles
 */
// TODO get point size working
public class JmePointCloudPanel extends PointCloudPanel {

	final JmeBridgeToAwt bridge;

	public JmePointCloudPanel(JmeBridgeToAwt bridge) {
		this.bridge = bridge;
		add(bridge.getCanvas());
	}

	@Override
	public void setFov(double angle) {

	}

	@Override
	public void setCamera(Se3_F64 camera) {

	}

	@Override
	public void setShowAxis(boolean show) {

	}

	@Override
	public boolean getShowAxis() {
		return false;
	}

	@Override
	public void addPoints(List<Point3D_F64> points, final int color , final float size ) {
		final float[] buf = convertPointsToArray(points);

		bridge.enqueue(new Callable<Void>(){
			public Void call(){
				float red = ((color >> 16) & 0xFF ) / 255.0f;
				float green = ((color >> 8) & 0xFF ) / 255.0f;
				float blue = (color & 0xFF ) / 255.0f;

				Material mat = new Material(bridge.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
				mat.setColor("Color", new ColorRGBA(red,green,blue,1.0f) );
				mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

				Mesh m = new Mesh();
				m.setBuffer(VertexBuffer.Type.Position, 3, buf);
				m.setMode(Mesh.Mode.Points);
				m.setPointSize(size);
				m.setStatic();
				m.updateBound();

				Geometry g = new Geometry("Point Cloud", m);
				g.setShadowMode(RenderQueue.ShadowMode.Off);
				g.setQueueBucket(RenderQueue.Bucket.Opaque);
				g.setMaterial(mat);
				bridge.getRootNode().attachChild(g);
				return null;
			}});

	}

	@Override
	public void addPoints(List<Point3D_F64> points, int[] colors , final float size ) {
		final float[] buffPoints = convertPointsToArray(points);
		final float[] buffColor = new float[4*points.size()];

		for (int i = 0; i < points.size(); i++) {
			int color = colors[i];

			float red = ((color >> 16) & 0xFF ) / 255.0f;
			float green = ((color >> 8) & 0xFF ) / 255.0f;
			float blue = (color & 0xFF ) / 255.0f;

			buffColor[i*4+0] = red;
			buffColor[i*4+1] = green;
			buffColor[i*4+2] = blue;
			buffColor[i*4+3] = 1.0f;
		}

		bridge.enqueue(new Callable<Void>(){
			public Void call(){

				Material mat = new Material(bridge.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
				mat.getAdditionalRenderState().setPointSprite(true);
				mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
				mat.setBoolean("PointSprite", true);


				Mesh m = new Mesh();
				m.setBuffer(VertexBuffer.Type.Position, 3, buffPoints);
				m.setBuffer(VertexBuffer.Type.Color, 4, buffColor);
				m.setMode(Mesh.Mode.Points);
				m.setPointSize(size);
				m.setStatic();
				m.updateBound();

				Geometry g = new Geometry("Point Cloud", m);
				g.setShadowMode(RenderQueue.ShadowMode.Off);
				g.setQueueBucket(RenderQueue.Bucket.Opaque);
				g.setMaterial(mat);
				bridge.getRootNode().attachChild(g);
				return null;
			}});
	}

	@Override
	public void shutdownVisualize() {
		// wait for it to finish shutting down
		bridge.stop(true);
	}

	private float[] convertPointsToArray(List<Point3D_F64> points) {
		int N = points.size();

		final float buf[] = new float[N*3];
		for( int i = 0; i < N; i++ ) {
			Point3D_F64 p = points.get(i);
			buf[i*3+0] = (float)p.x;
			buf[i*3+1] = (float)p.y;
			buf[i*3+2] = (float)p.z;
		}
		return buf;
	}
}