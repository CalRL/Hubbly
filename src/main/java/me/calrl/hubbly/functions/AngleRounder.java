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