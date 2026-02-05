package me.calrl.hubbly.utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AngleRounderTests {
    @Test
    void round_returnsExactAngle_whenAlreadyOnTarget() {
        AngleRounder rounder = new AngleRounder(0);

        assertEquals(0f, rounder.round(0));
        assertEquals(45f, rounder.round(45));
        assertEquals(-90f, rounder.round(-90));
        assertEquals(135f, rounder.round(135));
    }

    @Test
    void round_roundsToNearestTargetAngle() {
        AngleRounder rounder = new AngleRounder(0);

        assertEquals(0f, rounder.round(10));
        assertEquals(45f, rounder.round(30));
        assertEquals(90f, rounder.round(80));
        assertEquals(-45f, rounder.round(-30));
        assertEquals(-90f, rounder.round(-100));
    }

    @Test
    void round_prefersLowerAngleOnTie() {
        AngleRounder rounder = new AngleRounder(0);

        // Exactly halfway between 0 and 45 → should choose 0
        assertEquals(0f, rounder.round(22.5f));

        // Halfway between -45 and 0 → should choose -45
        assertEquals(-45f, rounder.round(-22.5f));
    }

    @Test
    void getRoundedAngle_usesStoredAngle() {
        AngleRounder rounder = new AngleRounder(44);

        assertEquals(45f, rounder.getRoundedAngle());
    }

    @Test
    void setAngle_updatesRoundedResult() {
        AngleRounder rounder = new AngleRounder(10);
        assertEquals(0f, rounder.getRoundedAngle());

        rounder.setAngle(80);
        assertEquals(90f, rounder.getRoundedAngle());
    }

    @Test
    void getAngle_returnsRawAngle() {
        AngleRounder rounder = new AngleRounder(33.3f);

        assertEquals(33.3f, rounder.getAngle());
    }
}
