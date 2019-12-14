import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/*
Justin Luk 11723 Canton Gearhounds
Our first actually decent TeleOp this year
 */

public class Skystone1 extends LinearOpMode {
    private DcMotor RF,RB,LF,LB,FI,Cranemotor;
    private Servo Arm = null;
    private CRServo Crane1 = null;
    BNO055IMU imu;
    Orientation lastAngles = new Orientation();
    double                  globalAngle, pl = .5,pr = .5, correction;
    boolean                 aButton, bButton, touched;
    @Override
    public void runOpMode() throws InterruptedException {
        RF = hardwareMap.dcMotor.get("RF"); //gets RFM on hardware map
        RB = hardwareMap.dcMotor.get("RB"); //gets RBM on hardware map
        LF = hardwareMap.dcMotor.get("LF"); //gets LFM on hardware map
        LB = hardwareMap.dcMotor.get("LB"); //gets LBM on hardware map
        FI = hardwareMap.dcMotor.get("FI"); //gets Front Intake on hardware map
        int Mode =0;
        Cranemotor = hardwareMap.dcMotor.get("Cranemotor");
        Arm = hardwareMap.servo.get("ARM");
        Crane1  = hardwareMap.crservo.get("Crane1");


        RB.setDirection(DcMotor.Direction.REVERSE); //sets both left side motors on reverse
        RF.setDirection(DcMotor.Direction.REVERSE);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        telemetry.addData("Mode", "calibrating...");
        telemetry.update();

        while (!isStopRequested() && !imu.isGyroCalibrated())
        {
            sleep(50);
            idle();
        }

        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", imu.getCalibrationStatus().toString());
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            if (Mode == 0) {
                telemetry.addLine("Arcade Drive");
                double px = gamepad1.left_stick_x;
                if (Math.abs(px) < 0.05) px = 0;
                double py = -gamepad1.left_stick_y;
                if (Math.abs(py) < 0.05) py = 0;
                double pa = -gamepad1.right_stick_x;
                if (Math.abs(pa) < 0.05) pa = 0;
                double plf = -px + py - pa;
                double plb = px + py + -pa;
                double prf = -px + py + pa;
                double prb = px + py + pa;
                double max = Math.max(1.0, Math.abs(plf));
                max = Math.max(max, Math.abs(plb));
                max = Math.max(max, Math.abs(prf));
                max = Math.max(max, Math.abs(prb));
                plf /= max;
                plb /= max;
                prf /= max;
                prb /= max;
                LF.setPower(plf);
                LB.setPower(plb);
                RF.setPower(prf);
                RB.setPower(prb);

                while (gamepad2.right_trigger > 0) {
                    Cranemotor.setPower(0.5);
                }
                while (gamepad2.left_trigger > 0) {
                    Cranemotor.setPower(-0.5);
                }
                if (gamepad2.right_bumper){
                    Crane1.setPower(0.5);
                }
                if (gamepad2.left_bumper) {
                    Crane1.setPower(-0.5);
                }
                while (gamepad1.left_stick_x < 0)    {
                    LF.setPower(-1);
                    LB.setPower(1);
                    RF.setPower(-1);
                    RB.setPower(1);
                }

                while (gamepad1.left_stick_x > 0)       {
                    LF.setPower(1);
                    LB.setPower(-1);
                    RF.setPower(1);
                    RB.setPower(-1);
                }

                while (gamepad1.right_bumper)    {
                    LF.setPower(-0.5);
                    LB.setPower(0.5);
                    RF.setPower(-0.5);
                    RB.setPower(0.5);
                }

                while (gamepad1.left_bumper)       {
                    LF.setPower(0.5);
                    LB.setPower(-0.5);
                    RF.setPower(0.5);
                    RB.setPower(-0.5);
                }
                if (gamepad1.a) {
                    Mode ++;
                }

            }

            if (Mode == 1) {
                telemetry.addLine("Tank Drive");
                double pr = gamepad1.right_stick_y;
                if (Math.abs(pr) < 0.05) pr = 0;
                double pl = gamepad1.left_stick_y;
                if (Math.abs(pl) < 0.05) pl = 0;
                double plf = pl;
                double plb = pl;
                double prf = pr;
                double prb = pr;
                double max = Math.max(1.0, Math.abs(plf));
                max = Math.max(max, Math.abs(plb));
                max = Math.max(max, Math.abs(prf));
                max = Math.max(max, Math.abs(prb));
                plf /= max;
                plb /= max;
                prf /= max;
                prb /= max;

                LF.setPower(plf);
                LB.setPower(plb);
                RF.setPower(prf);
                RB.setPower(prb);

                while (gamepad2.right_trigger > 0) {
                    Cranemotor.setPower(0.5);
                }
                while (gamepad2.left_trigger > 0) {
                    Cranemotor.setPower(-0.5);
                }
                if (gamepad2.right_bumper){
                    Crane1.setPower(0.5);
                }
                if (gamepad2.left_bumper) {
                    Crane1.setPower(-0.5);
                }
                while (gamepad1.right_trigger > 0)    {
                    LF.setPower(-1);
                    LB.setPower(1);
                    RF.setPower(-1);
                    RB.setPower(1);
                }

                while (gamepad1.left_trigger > 0)       {
                    LF.setPower(1);
                    LB.setPower(-1);
                    RF.setPower(1);
                    RB.setPower(-1);
                }

                while (gamepad1.right_bumper)    {
                    LF.setPower(-0.5);
                    LB.setPower(0.5);
                    RF.setPower(-0.5);
                    RB.setPower(0.5);
                }

                while (gamepad1.left_bumper)       {
                    LF.setPower(0.5);
                    LB.setPower(-0.5);
                    RF.setPower(0.5);
                    RB.setPower(-0.5);
                }

                if (gamepad1.a){
                    Mode --;
                }
                if (gamepad1.b) {

                }
            }
            if (Mode == 2) {
                telemetry.addLine("Field Centric Drive");
                correction = checkDirection();

                telemetry.addData("1 imu heading", lastAngles.firstAngle);
                telemetry.addData("2 global heading", globalAngle);
                telemetry.addData("3 correction", correction);
                telemetry.update();

                LF.setPower(pl - correction);
                RF.setPower(pr + correction);
                LB.setPower(pl - correction);
                RB.setPower(pr + correction);

                double pr = gamepad1.right_stick_y;
                if (Math.abs(pr) < 0.05) pr = 0;
                double pl = gamepad1.left_stick_y;
                if (Math.abs(pl) < 0.05) pl = 0;
                double plf = pl;
                double plb = pl;
                double prf = pr;
                double prb = pr;
                double max = Math.max(1.0, Math.abs(plf));
                max = Math.max(max, Math.abs(plb));
                max = Math.max(max, Math.abs(prf));
                max = Math.max(max, Math.abs(prb));
                plf /= max;
                plb /= max;
                prf /= max;
                prb /= max;

                LF.setPower(plf);
                LB.setPower(plb);
                RF.setPower(prf);
                RB.setPower(prb);





            }
            if (Mode == 3) {
                telemetry.addLine("Exponential Drive");
                while (gamepad1.left_stick_y > 0.05) {
                    double pl = Math.abs(gamepad1.left_stick_y);
                    if (Math.abs(pl) < 0.05) pl = 0;
                    double plf = Math.pow(pl,1.4);
                    double plb = Math.pow(pl,1.4);
                    double max = Math.max(1.0, Math.abs(plf));
                    max = Math.max(max, Math.abs(plb));
                    plf /= max;
                    plb /= max;

                    LF.setPower(plf);
                    LB.setPower(plb);
                }
                while (gamepad1.left_stick_y < -0.05) {
                    double pl = Math.abs(gamepad1.left_stick_y);
                    if (Math.abs(pl) < 0.05) pl = 0;
                    double plf = -1*Math.pow(pl,1.4);
                    double plb = -1*Math.pow(pl,1.4);
                    double max = Math.max(1.0, Math.abs(plf));
                    max = Math.max(max, Math.abs(plb));
                    plf /= max;
                    plb /= max;

                    LF.setPower(plf);
                    LB.setPower(plb);
                }
                while (gamepad1.right_stick_y > 0.05){
                    double pr = Math.abs(gamepad1.right_stick_y);
                    if (Math.abs(pr) < 0.05) pr = 0;

                    double prf = Math.pow(pr,1.4);
                    double prb = Math.pow(pr,1.4);
//                    max = Math.max(max, Math.abs(p3));
//                    max = Math.max(max, Math.abs(p4));
                    RF.setPower(prf);
                    RB.setPower(prb);
//                    p3 /= max;
//                    p4 /= max;
                }
                while (gamepad1.right_stick_y < -0.05){
                    double pr = Math.abs(gamepad1.right_stick_y);
                    if (Math.abs(pr) < 0.05) pr = 0;

                    double prf = -1*Math.pow(pr,1.4);
                    double prb = -1*Math.pow(pr,1.4);
//                    max = Math.max(max, Math.abs(p3));
//                    max = Math.max(max, Math.abs(p4));
                    RF.setPower(prf);
                    RB.setPower(prb);
//                    p3 /= max;
//                    p4 /= max;
                }








            }

        }

    }
    private double checkDirection()
    {
        // The gain value determines how sensitive the correction is to direction changes.
        // You will have to experiment with your robot to get small smooth direction changes
        // to stay on a straight line.
        double correction, angle, gain = .10;

        angle = getAngle();

        if (angle == 0)
            correction = 0;             // no adjustment.
        else
            correction = -angle;        // reverse sign of angle for correction.

        correction = correction * gain;

        return correction;
    }
    private double getAngle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }



}

