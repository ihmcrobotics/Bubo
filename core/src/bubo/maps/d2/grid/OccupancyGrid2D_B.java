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

package bubo.maps.d2.grid;

/**
 * Interface for an boolean occupancy grid.
 *
 * @author Peter Abeles
 */
public interface OccupancyGrid2D_B extends OccupancyGrid2D {

    /**
     * Sets every cell in the map to the specified value.
     *
     * @param value The new value of each cell in the map.
     */
    public void setAll( boolean value );

    /**
     * Sets the specified cell to 'value'.
     *
     * @param x x-coordinate of the cell.
     * @param y y-coordinate of the cell.
     * @param value The cell's new value.
     */
    public void set( int x , int y , boolean value );

    /**
     * Gets the value of the cell at the specified coordinate.
     *
     * @param x x-coordinate of the cell.
     * @param y y-coordinate of the cell.
     * @return The cell's value.
     */
    public boolean get( int x , int y );

}