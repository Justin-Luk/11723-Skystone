import android.hardware.Camera;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.Timer;

import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.ZYX;

@Autonomous (name = "TheActualAuto?")
public class Combinetime extends LinearOpMode {

    DcMotor LF, RF, LB, RB, FI, CraneMotor; // Defines names of hardware
    CRServo BOOM;
    Servo Arm2, Stone;
    int MAX_TRIES;
    int Where =0;
    BNO055IMU imu;
    private Orientation angles;

    double kP = 0.005;
    double kD = 0.01;
    double kI = 0.00008;

    double totalError = 0;
    double lastAngle = 0;

    private ElapsedTime runtime = new ElapsedTime();

    double ticksPerMotorRev = 383.6;        //sets values we will need later
    double driveGearReduction = 1;
    double wheelDiameterInches = 3.93701;
    double ticksPerInch = (ticksPerMotorRev * driveGearReduction) / (wheelDiameterInches * 3.14159265359);

    double stop = 0;


    private static final String VUFORIA_KEY =
            "AXxGX4//////AAABmaGzhDtLe0vztwUuFzptF78cmvdtkCQa4TLJaygK9Mued0mzNi3KaHkbVeeN1llvJDgiTItqnqEHP1SosYrZk3gZ948OKIw39IGN9dy+MV2AbXcAZEgkl26O6oK+Fr5728OXW75g04pt4+DRuf4GiUQgr6gBjJg0nbRV/7VzlYLwXHKrOK5SJ9rLugJ/rwsw1aVfJAwamNf4YNIaSh3SQgw0dL+nALMxEOC9Hb8aPSijZkW66JMgOz9bYJXZlJUGtRTodc8xes544zLyRNQx5j5aa0onYRADaqtcoNF2bw7PtgZCt0uDHJa+J1+5RZF0IS4X+Otj5VyxOC2z9kAMtbeLG90n71dYmRGgbAAk1DhO";

    private VuforiaLocalizer vuforia = null;
    private ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    WebcamName webcamName = null;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "Initialized");
        telemetry.update();

        RF = hardwareMap.dcMotor.get("RF");     //gets each motor and servo from hardware map
        RB = hardwareMap.dcMotor.get("RB");
        LF = hardwareMap.dcMotor.get("LF");
        LB = hardwareMap.dcMotor.get("LB");
        FI = hardwareMap.dcMotor.get("FI");
        CraneMotor = hardwareMap.dcMotor.get("LIFT");

        telemetry.addData("timer", time);
        Arm2 = hardwareMap.servo.get("ARM2");
        Stone = hardwareMap.servo.get("Stone");
        BOOM = hardwareMap.crservo.get("BOOM");

        Stone.setPosition(1);

        Camera cam = Camera.open();
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

        RF.setDirection(DcMotor.Direction.REVERSE);
        RB.setDirection(DcMotor.Direction.REVERSE);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        /*
         * We also indicate which camera on the RC we wish to use.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        VuforiaTrackables targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");
        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");

        LF.setPower(0);
        LB.setPower(0);
        RF.setPower(0);
        RB.setPower(0);
        FI.setPower(0);
        CraneMotor.setPower(0);

        // double drivePower = .4;
        // double turnPower = .2;

        BOOM.setPower(0);

        BNO055IMU.Parameters parameters1 = new BNO055IMU.Parameters();       //sets up IMU
        parameters1.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters1.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters1.calibrationDataFile = "REVHub1IMUCalibration.json";
        parameters1.loggingEnabled      = true;
        parameters1.loggingTag          = "IMU";

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters1);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        Arm2.setPosition(.6);
        MAX_TRIES=0;

        telemetry.addLine("ready to go!");
        telemetry.update();


        waitForStart();

        while (opModeIsActive()) {
            encoderDrive(.4, -10, 31, false);
            encoderDrive(.5, 10, 31, true);
            encoderDrive(.4, -18, 31, false);
            Arm2.setPosition(0);
            sleep(250);
            encoderDrive(.4,32.5,31, false);
            //encoderDrive(.2,-3,31, false);
            Arm2.setPosition(.6);
            gyroTurn(-85);
            encoderDrive(.6, -69, 31, false);
            gyroTurn(-168);
            /*RF.setPower(-.3);
            RB.setPower(-.3);
            LF.setPower(.3);
            LB.setPower(.3);
            sleep(100);*/
            //encoderDrive(.5, 4, 31, false);
            sleep(50);
            encoderDrive(.3, 13, 31, false);
            CraneMotor.setPower(1);
            sleep(500);
            CraneMotor.setPower(0);
            // encoderDrive(.5, 3, 31, true);




            targetsSkyStone.activate();     //time to start scanning!
            while (!isStopRequested()) {

                if(timer.milliseconds() == 3) continue;
                boolean stoneVisible = false;


                // check to see if the skystone is visible.
                if (((VuforiaTrackableDefaultListener) stoneTarget.getListener()).isVisible()) {
                    telemetry.addData("Visible Target", stoneTarget.getName()); //just returns "Stone Target"
                /*In these coordinates, the X axis goes from the left (negative) to the right (positive).
                    The Y axis goes up and down on the middle of the screen, and the Z axis goes from the camera outward. */

                    //command to get the relative position as provided by vuforia
                    OpenGLMatrix location = ((VuforiaTrackableDefaultListener) stoneTarget.getListener()).getVuforiaCameraFromTarget();
                    if (location != null) {
                        // Get the positional part of the coordinates
                        VectorF translation = location.getTranslation();
                        //clip the actual X to see if it is closer to the left or right
                        float closestX = Range.clip(translation.get(0), -20f, 20f);
                    /*"center" because we only look at the right two in the farthest set of three in the quarry,
                    so the leftmost image would be the center of the three stones concerned */
                        if (closestX == -20) {
                            telemetry.addData("Skystone Target:", "Center");
                            Where = 1;
                        }

                        //Right most stone of the two
                        if (closestX == 20) {
                            telemetry.addData("Skystone Target:", "Right");
                            Where = 2;
                        }

                        //Also express the relative pose (for info purposes)

                        telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                                translation.get(0), translation.get(1), translation.get(2));
                    }
                } else {
                    telemetry.addData("Visible Target", "none");
                    Where = 3; //if stone isn't found

                    encoderDrive(1, 4.05, 60, true);

                    RF.setPower(stop);
                    RB.setPower(stop);
                    LF.setPower(stop);
                    LB.setPower(stop);
                    sleep(300);
                    MAX_TRIES ++;
                    if (MAX_TRIES > 6) {
                        encoderDrive(1,4, 60, false);
                        encoderDrive(.4, 14, 60, false);
                        Stone.setPosition(.2);
                        CraneMotor.setPower(-1);
                        sleep(400);
                        CraneMotor.setPower(0);
                      RF.setPower(.5);
                      RB.setPower(.5);
                      LF.setPower(-.5);
                      LB.setPower(-.5);
                      sleep(250);
                        gyroTurn(90);
                        encoderDrive(.5, -16, 60, true);
                        encoderDrive(.3, 45, 60, false );
                        Stone.setPosition(1);
                        encoderDrive(.3, -10, 60, false);
                        stop();
                    }

                    if (25 == time) {
                        encoderDrive(1,4, 60, false);
                        encoderDrive(.4, 14, 60, false);
                        Stone.setPosition(.2);
                        CraneMotor.setPower(-1);
                        sleep(400);
                        CraneMotor.setPower(0);
                        BNO055IMU.Parameters parameters2 = new BNO055IMU.Parameters();       //sets up IMU
                        parameters2.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
                        parameters2.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
                        parameters2.calibrationDataFile = "REVHub1IMUCalibration.json";
                        parameters2.loggingEnabled      = true;
                        parameters2.loggingTag          = "IMU";

                        imu = hardwareMap.get(BNO055IMU.class, "imu");
                        imu.initialize(parameters2);

                        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
                        sleep(700);
                        gyroTurn(90);
                        encoderDrive(.5, -16, 60, true);
                        encoderDrive(.3, 45, 60, false );
                        Stone.setPosition(1);
                        encoderDrive(.3, -10, 60, false);
                        stop();
                    }

                    telemetry.update();
                    timer.reset();

                }
                telemetry.update();
                timer.reset();

                if (Where == 1) {
                    telemetry.addLine("Position 1");
                    telemetry.update();
                    encoderDrive(1,4, 60, false);
                    encoderDrive(.4, 14, 60, false);
                    Stone.setPosition(.2);
                    CraneMotor.setPower(-1);
                    sleep(400);
                    CraneMotor.setPower(0);

                    BNO055IMU.Parameters parameters2 = new BNO055IMU.Parameters();       //sets up IMU
                    parameters2.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
                    parameters2.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
                    parameters2.calibrationDataFile = "REVHub1IMUCalibration.json";
                    parameters2.loggingEnabled      = true;
                    parameters2.loggingTag          = "IMU";

                    imu = hardwareMap.get(BNO055IMU.class, "imu");
                    imu.initialize(parameters2);

                    imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
                    sleep(700);
                    gyroTurn(90);
                    encoderDrive(.5, -16, 60, true);
                    encoderDrive(.3, 45, 60, false );
                    Stone.setPosition(1);
                    encoderDrive(.3, -10, 60, false);
                    stop();
                }
                if (Where == 2) {
                    telemetry.addLine("Position 2");
                    telemetry.update();
                    encoderDrive(1,4, 60, false);
                    encoderDrive(.4, 14, 60, false);
                    Stone.setPosition(.2);
                    CraneMotor.setPower(-1);
                    sleep(400);
                    CraneMotor.setPower(0);
                    BNO055IMU.Parameters parameters2 = new BNO055IMU.Parameters();       //sets up IMU
                    parameters2.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
                    parameters2.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
                    parameters2.calibrationDataFile = "REVHub1IMUCalibration.json";
                    parameters2.loggingEnabled      = true;
                    parameters2.loggingTag          = "IMU";

                    imu = hardwareMap.get(BNO055IMU.class, "imu");
                    imu.initialize(parameters2);

                    imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
                    sleep(700);
                    gyroTurn(90);
                    encoderDrive(.5, -16, 60, true);
                    encoderDrive(.3, 45, 60, false );
                    Stone.setPosition(1);
                    encoderDrive(.3, -10, 60, false);
                    stop();
                }
                if (Where == 4) {
                    telemetry.addLine("Position 3");
                    telemetry.update();
                    encoderDrive(1,4, 60, false);
                    encoderDrive(.4, 14, 60, false);
                    Stone.setPosition(.2);
                    CraneMotor.setPower(-1);
                    sleep(400);
                    CraneMotor.setPower(0);
                    BNO055IMU.Parameters parameters2 = new BNO055IMU.Parameters();       //sets up IMU
                    parameters2.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
                    parameters2.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
                    parameters2.calibrationDataFile = "REVHub1IMUCalibration.json";
                    parameters2.loggingEnabled      = true;
                    parameters2.loggingTag          = "IMU";

                    imu = hardwareMap.get(BNO055IMU.class, "imu");
                    imu.initialize(parameters2);

                    imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
                    sleep(700);

                    gyroTurn(90);
                    encoderDrive(.5, -16, 60, true);
                    encoderDrive(.3, 45, 60, false );
                    Stone.setPosition(1);
                    encoderDrive(.3, -10, 60, false);
                    stop();

                }
            }

        }
    }

    private void encoderDrive(double speed, double inches, double timeoutS, boolean strafe) {

        telemetry.addLine("Encoder Drive");

        int newLFTarget;
        int newRFTarget;
        int newLBTarget;
        int newRBTarget;
        int lFPos = LF.getCurrentPosition();
        int rFPos = RF.getCurrentPosition();
        int lBPos = LB.getCurrentPosition();
        int rBPos = RB.getCurrentPosition();
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, ZYX, AngleUnit.DEGREES);
        double startAngle = angles.firstAngle;

        if (opModeIsActive()) {
            if (strafe) {
                newLFTarget = lFPos + (int) (inches * ticksPerInch);
                newRFTarget = rFPos - (int) (inches * ticksPerInch);
                newLBTarget = lBPos - (int) (inches * ticksPerInch);
                newRBTarget = rBPos + (int) (inches * ticksPerInch);
            } else {
                newLFTarget = lFPos + (int) (inches * ticksPerInch);
                newRFTarget = rFPos + (int) (inches * ticksPerInch);
                newLBTarget = lBPos + (int) (inches * ticksPerInch);
                newRBTarget = rBPos + (int) (inches * ticksPerInch);
            }

            telemetry.addData("speed", speed);
            telemetry.addData("inches", inches);
            telemetry.addData("newLFTarget", newLFTarget);
            telemetry.addData("newRFTarget", newRFTarget);
            telemetry.addData("newLBTarget", newLBTarget);
            telemetry.addData("newRBTarget", newRBTarget);

            LF.setTargetPosition(newLFTarget);
            RF.setTargetPosition(newRFTarget);
            LB.setTargetPosition(newLBTarget);
            RB.setTargetPosition(newRBTarget);

            LF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            RF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            LB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            RB.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            LF.setPower(Math.abs(speed));
            RF.setPower(Math.abs(speed));
            LB.setPower(Math.abs(speed));
            RB.setPower(Math.abs(speed));

            runtime.reset();
            while (opModeIsActive() && runtime.seconds() < timeoutS && (LF.isBusy() && RF.isBusy() && LB.isBusy() && RB.isBusy())) {
                if (!strafe) {
                    double error = kP * (startAngle - angles.firstAngle);
                    angles = imu.getAngularOrientation(AxesReference.INTRINSIC, ZYX, AngleUnit.DEGREES);
                    telemetry.addData("Start Angle", startAngle);
                    telemetry.addData("Current Angle", angles.firstAngle);
                    telemetry.addData("error", error);
                    telemetry.update();
                    if (error > 0) {
                        LF.setPower(Math.abs(speed) - error);
                        RF.setPower(Math.abs(speed) + error);
                        LB.setPower(Math.abs(speed) - error);
                        RB.setPower(Math.abs(speed) + error);
                    } else if (error < 0) {
                        LF.setPower(Math.abs(speed) + error);
                        RF.setPower(Math.abs(speed) - error);
                        LB.setPower(Math.abs(speed) + error);
                        RB.setPower(Math.abs(speed) - error);
                    }
                }
                telemetry.addData("LF Current Position", LF.getCurrentPosition());
                telemetry.addData("RF Current Position", RF.getCurrentPosition());
                telemetry.addData("LB Current Position", LB.getCurrentPosition());
                telemetry.addData("RB Current Position", RB.getCurrentPosition());
                telemetry.addData("LF Current Power", LF.getPower());
                telemetry.addData("RF Current Power", RF.getPower());
                telemetry.addData("LB Current Power", LB.getPower());
                telemetry.addData("RB Current Power", RB.getPower());
                telemetry.update();
            }
            LF.setPower(0);
            RF.setPower(0);
            LB.setPower(0);
            RB.setPower(0);

            LF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            RF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            LB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            RB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(100);
        }
    }

    private void gyroTurn(double targetAngle) {
        //+ is counter-clockwise
        //- is clockwise
        boolean finished = false;
        while (!finished) {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, ZYX, AngleUnit.DEGREES);
            double currentAngle = angles.firstAngle;
            double e = (targetAngle - currentAngle);
            totalError += e;
            double error = (kP * e) - (kD * (currentAngle - lastAngle)) + (kI) * (totalError);
            lastAngle = currentAngle;
            LF.setPower(-error);
            RF.setPower(error);
            LB.setPower(-error);
            RB.setPower(error);
            telemetry.addData("targetAngle", targetAngle);
            telemetry.addData("currentAngle", currentAngle);
            telemetry.addData("error", error);
            telemetry.addData("targetAngle - currentAngle", targetAngle - currentAngle);
            telemetry.addData("finished", finished);



            telemetry.addData("LFM Current Power", LF.getPower());
            telemetry.addData("RFM Current Power", RF.getPower());
            telemetry.addData("LBM Current Power", LB.getPower());
            telemetry.addData("RBM Current Power", RB.getPower());

            telemetry.update();
            if (Math.abs(targetAngle - currentAngle) < 4) {
                finished = true;
                telemetry.addData("Finished", finished);
                RF.setPower(0);
                RB.setPower(0);
                LF.setPower(0);
                LB.setPower(0);
                sleep(500);
            }
        }
    }
}