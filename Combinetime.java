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

import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.ZYX;

@Autonomous
public class Combinetime extends LinearOpMode {

    DcMotor LF, RF, LB, RB, FI, CraneMotor; // Defines names of hardware
    CRServo Crane1;
    Servo Arm2;

    int Where =0;
    BNO055IMU imu;
    private Orientation angles;

    double kP = 0.01;
    double kD = 0.0123;
    double kI = 0.001;

    double totalError = 0;
    double lastAngle = 0;

    private ElapsedTime runtime = new ElapsedTime();

    double ticksPerMotorRev = 383.6;        //sets values we will need later
    double driveGearReduction = 0.5;
    double wheelDiameterInches = 3.93701;
    double ticksPerInch = (ticksPerMotorRev * driveGearReduction) / (wheelDiameterInches * 3.14159265359);

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

        Arm2 = hardwareMap.servo.get("ARM2");
        Crane1 = hardwareMap.crservo.get("BOOM");

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

        double drivePower = .4;
        double turnPower = .2;

        Crane1.setPower(0);

        BNO055IMU.Parameters parameters1 = new BNO055IMU.Parameters();       //sets up IMU
        parameters1.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters1.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters1.calibrationDataFile = "REVHub1IMUCalibration.json";
        parameters1.loggingEnabled      = true;
        parameters1.loggingTag          = "IMU";

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters1);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        telemetry.addLine("ready to go!");
        telemetry.update();


        waitForStart();

        while (opModeIsActive()) {

            encoderDrive(-0.5, 60, 60, false);      //put the foundation in the zone
            stop();                             //back up
            Arm2.setPosition(.8);
            encoderDrive(1, 12, 60, false);
            gyroTurn(90);
            Arm2.setPosition(0);

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

                    encoderDrive(.5, 8, 60, true);

                }
                telemetry.update();
                timer.reset();

                if (Where == 1) {
                    telemetry.addLine("Position 1");
                    telemetry.update();

                }
                if (Where == 2) {
                    telemetry.addLine("Position 2");
                    telemetry.update();




                }
                if (Where == 4) {
                    telemetry.addLine("Position 3");
                    telemetry.update();

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
        //+ counter-clockwise
        //- clockwise
        boolean finished = false;
        while (!finished) {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, ZYX, AngleUnit.DEGREES);
            double currentAngle = angles.firstAngle;
            double e = (targetAngle - currentAngle);
            totalError+=e;
            double error = (kP * e)-(kD*(currentAngle-lastAngle))+(kI)*(totalError);
            lastAngle = currentAngle;
            LF.setPower((Math.log(error+.1)));
            RF.setPower(-Math.log(error+.1));
            LB.setPower(Math.log(error+.1));
            RB.setPower(-Math.log(error+.1));
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

                sleep(1000);
            }
        }
    }
}