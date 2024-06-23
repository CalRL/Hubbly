/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */

package me.calrl.hubbly.functions;
public class AngleRounder {

    // Method to round an angle to the nearest 0, 90, 180, or 270 degrees
    public static float roundToNearestRightAngle(float angle) {
        // Define the target angles
        float[] targetAngles = {-135, -90, -45, 0, 45, 90, 135};

        // Initialize the closest angle as the first target angle
        float closestAngle = targetAngles[0];
        float smallestDifference = Math.abs(angle - closestAngle);

        // Iterate through target angles to find the closest one
        for (float target : targetAngles) {
            float difference = Math.abs(angle - target);
            if (difference < smallestDifference) {
                closestAngle = target;
                smallestDifference = difference;
            }
        }

        return closestAngle;
    }
}