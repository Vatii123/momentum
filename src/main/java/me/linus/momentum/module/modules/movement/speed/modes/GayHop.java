package me.linus.momentum.module.modules.movement.speed.modes;

import me.linus.momentum.event.events.player.MoveEvent;
import me.linus.momentum.module.modules.movement.Speed;
import me.linus.momentum.module.modules.movement.speed.SpeedMode;
import me.linus.momentum.util.client.MessageUtil;
import me.linus.momentum.util.player.MotionUtil;

/**
 * @author linustouchtips
 * @since 02/03/2021
 */

public class GayHop extends SpeedMode {

    @Override
    public void onMotionUpdate() {
        lastDist = Math.sqrt(Math.pow(mc.player.posX - mc.player.prevPosX, 2) + Math.pow(mc.player.posZ - mc.player.prevPosZ, 2));
    }

    @Override
    public void handleSpeed(MoveEvent event) {
        MessageUtil.sendClientMessage("" + moveSpeed);

        if (MotionUtil.isMoving()) {
            if (mc.player.onGround)
                setStage(Stage.Jump);

            if (Speed.useTimer.getValue())
                mc.timer.tickLength = (float) (50f / Speed.timerTicks.getValue());
        }

        if (stage == Stage.Pre && MotionUtil.isMoving()) {
            setStage(Stage.Jump);
            moveSpeed = 1.38 * Speed.speed.getValue();
        }

        else if (stage == Stage.Jump) {
            setStage(Stage.Post);

            if (Speed.jump.getValue()) {
                mc.player.motionY = 0.3994f;
                event.setY(0.3994f);
            }

            moveSpeed *= 2.149;
        }

        else if (stage == Stage.Post) {
            setStage(Stage.Cycle);
            moveSpeed = lastDist - (0.66 * (lastDist - Speed.speed.getValue()));
        }

        else {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically)
                setStage(Stage.Pre);

            moveSpeed = lastDist - (lastDist / 159.0);
        }

        moveSpeed = Math.max(moveSpeed, Speed.speed.getValue());

        MotionUtil.impressMoveSpeed(event, moveSpeed, (float) Speed.stepHeight.getValue());
    }
}
