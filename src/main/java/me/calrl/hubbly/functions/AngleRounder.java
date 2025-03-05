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
    private float angle;
    public AngleRounder(float angle) {
        this.angle = angle;
    }

    public float round(float angle) {
        float[] targetAngles = {-135, -90, -45, 0, 45, 90, 135};

        float closestAngle = targetAngles[0];
        float smallestDifference = Math.abs(angle - closestAngle);

        for (float target : targetAngles) {
            float difference = Math.abs(angle - target);
            if (difference < smallestDifference) {
                closestAngle = target;
                smallestDifference = difference;
            }
        }

        return closestAngle;
    }

    public float getRoundedAngle() {
        return round(angle);
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}