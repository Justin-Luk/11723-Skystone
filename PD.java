import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.ZYX;

@Autonomous
public class PD extends LinearOpMode {

    DcMotor LF, RF, LB, RB, FI, LIFT; // Defines names of hardware
    CRServo BOOM;
    Servo ARM2;

    BNO055IMU imu;
    private Orientation angles;

    double kP = 0.005;
    double kD = 0.01;
    double kI = 0.00008;

    double totalError = 0;
    double lastAngle = 0;

    private ElapsedTime runtime = new ElapsedTime();

    double ticksPerMotorRev = 383.6;        //sets values we will need later
    double driveGearReduction = 0.5;
    double wheelDiameterInches = 3.93701;
    double ticksPerInch = (ticksPerMotorRev * driveGearReduction) / (wheelDiameterInches * 3.14159265359);

    //about 30" to foundation
    // 45" to line

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "Initialized");
        telemetry.update();

        RF = hardwareMap.dcMotor.get("RF");     //gets each motor and servo from hardware map
        RB = hardwareMap.dcMotor.get("RB");
        LF = hardwareMap.dcMotor.get("LF");
        LB = hardwareMap.dcMotor.get("LB");
        FI = hardwareMap.dcMotor.get("FI");
        LIFT = hardwareMap.dcMotor.get("LIFT");

        ARM2 = hardwareMap.servo.get("ARM2");
        BOOM = hardwareMap.crservo.get("BOOM");

        RF.setDirection(DcMotor.Direction.REVERSE);
        RB.setDirection(DcMotor.Direction.REVERSE);

        LF.setPower(0);
        LB.setPower(0);
        RF.setPower(0);
        RB.setPower(0);
        FI.setPower(0);
        LIFT.setPower(0);

        double drivePower = .4;
        double turnPower = .2;

        BOOM.setPower(0);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();       //sets up IMU
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "REVHub1IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        waitForStart();

        while (opModeIsActive()) {
           /*encoderDrive(.15,-21,60, false);
            ARM2.setPower(1);
            sleep(0);
            ARM2.setPower(0);
            encoderDrive(.15,20, 60, false);
            ARM2.setPower(-1);
            sleep(800);
            ARM2.setPower(0);*/

         //  encoderDrive(.25, 60, 60, false);
            gyroTurn(90);
            sleep(1000);
            RF.setPower(-.25);
            RB.setPower(-.25);
            LF.setPower(-.25);
            LB.setPower(-.25);
            sleep(2000);
          //  gyroTurn(180);


            //encoderDrive(.15,30,60, false);
           // stop();
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

            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, ZYX, AngleUnit.DEGREES);
            double startAngle = angles.firstAngle;

            while (opModeIsActive() && runtime.seconds() < timeoutS && (LF.isBusy() && RF.isBusy() && LB.isBusy() && RB.isBusy())) {
                if (!strafe) {
                    double error = 100 * (startAngle - angles.firstAngle);
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
            LF.setPower(-(error));
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
                telemetry.addLine("finished");
                RF.setPower(0);
                RB.setPower(0);
                LF.setPower(0);
                LB.setPower(0);
                sleep(500);
            }
        }
    }
}