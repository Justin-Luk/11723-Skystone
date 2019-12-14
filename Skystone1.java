import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/*
Justin Luk 11723 Canton Gearhounds
Our first actually decent TeleOp this year
 */

public class Skystone1 extends LinearOpMode {
    private DcMotor RF,RB,LF,LB,FI,Cranemotor;
    private Servo Arm = null;
    private CRServo Crane1 = null;
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

        waitForStart();
        while (opModeIsActive()) {
            if (Mode == 0) {
                double px = gamepad1.left_stick_x;
                if (Math.abs(px) < 0.05) px = 0;
                double py = -gamepad1.left_stick_y;
                if (Math.abs(py) < 0.05) py = 0;
                double pa = -gamepad1.right_stick_x;
                if (Math.abs(pa) < 0.05) pa = 0;
                double p1 = -px + py - pa;
                double p2 = px + py + -pa;
                double p3 = -px + py + pa;
                double p4 = px + py + pa;
                double max = Math.max(1.0, Math.abs(p1));
                max = Math.max(max, Math.abs(p2));
                max = Math.max(max, Math.abs(p3));
                max = Math.max(max, Math.abs(p4));
                p1 /= max;
                p2 /= max;
                p3 /= max;
                p4 /= max;
                LF.setPower(p1);
                LB.setPower(p2);
                RF.setPower(p3);
                RB.setPower(p4);

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
                double pr = gamepad1.right_stick_y;
                if (Math.abs(pr) < 0.05) pr = 0;
                double pl = gamepad1.left_stick_y;
                if (Math.abs(pl) < 0.05) pl = 0;
                double p1 = pl;
                double p2 = pl;
                double p3 = pr;
                double p4 = pr;

                LF.setPower(p1);
                LB.setPower(p2);
                RF.setPower(p3);
                RB.setPower(p4);

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
            }
            if (Mode == 2) {

            }

        }

    }
}

