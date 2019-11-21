import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@Autonomous(name="Does This work?", group ="Concept")
public class NewVuforiaTestTest extends LinearOpMode {
    private DcMotor RF,RB,LF,LB,FI;
    private Servo Arm = null;
    private int Where =0;
 
    // IMPORTANT: If you are using a USB WebCam, you must select CAMERA_CHOICE = BACK; and PHONE_IS_PORTRAIT = false;
    private static final String VUFORIA_KEY =
            "AXxGX4//////AAABmaGzhDtLe0vztwUuFzptF78cmvdtkCQa4TLJaygK9Mued0mzNi3KaHkbVeeN1llvJDgiTItqnqEHP1SosYrZk3gZ948OKIw39IGN9dy+MV2AbXcAZEgkl26O6oK+Fr5728OXW75g04pt4+DRuf4GiUQgr6gBjJg0nbRV/7VzlYLwXHKrOK5SJ9rLugJ/rwsw1aVfJAwamNf4YNIaSh3SQgw0dL+nALMxEOC9Hb8aPSijZkW66JMgOz9bYJXZlJUGtRTodc8xes544zLyRNQx5j5aa0onYRADaqtcoNF2bw7PtgZCt0uDHJa+J1+5RZF0IS4X+Otj5VyxOC2z9kAMtbeLG90n71dYmRGgbAAk1DhO";
 
    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
 
 
 
    // Class Members
    private VuforiaLocalizer vuforia = null;
    private ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
 
    /**
     * This is the webcam we are to use. As with other hardware devices such as motors and
     * servos, this device is identified using the robot configuration tool in the FTC application.
     */
    WebcamName webcamName = null;
 
 
 
    @Override public void runOpMode() {
        RF = hardwareMap.dcMotor.get("RF");
        RB = hardwareMap.dcMotor.get("RB");
        LF = hardwareMap.dcMotor.get("LF");
        LB = hardwareMap.dcMotor.get("LB");

        Arm = hardwareMap.servo.get("ARM");


        LF.setDirection(DcMotor.Direction.REVERSE);
        LB.setDirection(DcMotor.Direction.REVERSE);
 
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC phone);
         * If no camera monitor is desired, use the parameter-less constructor instead (commented out below).
         */
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
 
 
        // WARNING:
        // In this sample, we do not wait for PLAY to be pressed.  Target Tracking is started immediately when INIT is pressed.
        // This sequence is used to enable the new remote DS Camera Preview feature to be used with this sample.
        // CONSEQUENTLY do not put any driving commands in this loop.
        // To restore the normal opmode structure, just un-comment the following line:
 
        waitForStart();
        RF.setPower(1);
        RB.setPower(1);
        LF.setPower(1);
        LB.setPower(1);
        sleep(500);

        RF.setPower(1);
        RB.setPower(1);
        LF.setPower(-1);
        LB.setPower(-1);
        sleep(250);
 
        // Note: To use the remote camera preview:
        // AFTER you hit Init on the Driver Station, use the "options menu" to select "Camera Stream"
        // Tap the preview window to receive a fresh image.
 
        targetsSkyStone.activate();
        while (!isStopRequested()) {
            //Just  a refresh timer, don't actually need this
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
                    /*"center" because we (my team) only looks at the right two in the farthest set of three in the quarry,
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

            }
            telemetry.update();
            timer.reset();

            if (Where == 1) {
                telemetry.addLine("Position 1");
                RF.setPower(-0.5);
                RB.setPower(-0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(500);

                RF.setPower(0.5);
                RB.setPower(0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(1000);

                Arm.setPosition(1);

                FI.setPower(.5);
                sleep(1000);

                RF.setPower(-0.5);
                RB.setPower(-0.5);
                LF.setPower(-0.5);
                LB.setPower(-0.5);
                sleep(1000);

                RF.setPower(-0.5);
                RB.setPower(-0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(500);

                RF.setPower(0.5);
                RB.setPower(0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(5000);

                Arm.setPosition(0);
                FI.setPower(-.5);
                sleep(1000);



            }
            if (Where == 2) {
                telemetry.addLine("Position 2");
                RF.setPower(-0.5);
                RB.setPower(-0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(500);

                RF.setPower(0.5);
                RB.setPower(0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(1000);

                Arm.setPosition(1);

                FI.setPower(.5);
                sleep(1000);

                RF.setPower(-0.5);
                RB.setPower(-0.5);
                LF.setPower(-0.5);
                LB.setPower(-0.5);
                sleep(1000);

                RF.setPower(-0.5);
                RB.setPower(-0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(500);

                RF.setPower(0.5);
                RB.setPower(0.5);
                LF.setPower(0.5);
                LB.setPower(0.5);
                sleep(5000);

                Arm.setPosition(0);
                FI.setPower(-.5);
                sleep(1000);


            }
        }
 
        // Disable Tracking when we are done;
        targetsSkyStone.deactivate();
    }
}