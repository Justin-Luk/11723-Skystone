import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class testst extends OpMode {
    private DcMotor RF,RB,LF,LB,FI,Cranemotor;
    private CRServo Crane1, Arm2 = null;
    int pulse = 1680;


    @Override
    public void init() {

        RF = hardwareMap.dcMotor.get("RF"); //gets RFM on hardware map
        RB = hardwareMap.dcMotor.get("RB"); //gets RBM on hardware map
        LF = hardwareMap.dcMotor.get("LF"); //gets LFM on hardware map
        LB = hardwareMap.dcMotor.get("LB"); //gets LBM on hardware map
        FI = hardwareMap.dcMotor.get("FI"); //gets Front Intake on hardware map
        Cranemotor = hardwareMap.dcMotor.get("LIFT");
        Arm2 = hardwareMap.crservo.get("ARM2");
        Crane1  = hardwareMap.crservo.get("BOOM");


        RB.setDirection(DcMotor.Direction.FORWARD); //sets both left side motors on reverse
        LF.setDirection(DcMotor.Direction.FORWARD);
        LB.setDirection(DcMotor.Direction.FORWARD);

        RF.setDirection(DcMotor.Direction.REVERSE);

        RF.setPower(0); //establishes basic tank drive controls
        RB.setPower(0);
        LF.setPower(0);
        LB.setPower(0);


    }

    @Override
    public void loop() {
//        RF.setPower(0); //establishes basic tank drive controls
//        RB.setPower(0);
//        LF.setPower(0);
//        LB.setPower(0);
//        double pr = gamepad1.right_stick_y;
//        if (Math.abs(pr) < 0.05) pr = 0;
//        double pl = gamepad1.left_stick_y;
//        if (Math.abs(pl) < 0.05) pl = 0;
//        double plf = pl;
//        double plb = pl;
//        double prf = pr;
//        double prb = pr;
//        double max = Math.max(1.0, Math.abs(plf));
//        max = Math.max(max, Math.abs(plb));
//        max = Math.max(max, Math.abs(prf));
//        max = Math.max(max, Math.abs(prb));
//        plf /= max;
//        plb /= max;
//        prf /= max;
//        prb /= max;
//
//        LF.setPower(plf);
//        LB.setPower(plb);
//        RF.setPower(prf);
//        RB.setPower(prb);

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
//        RF.setPower(-(gamepad1.right_stick_y)); //establishes basic tank drive controls
//        RB.setPower(-(gamepad1.right_stick_y));
//        LF.setPower(-(gamepad1.left_stick_y));
//        LB.setPower(-(gamepad1.left_stick_y));
        Arm2.setPower(gamepad2.right_stick_y);
        Crane1.setPower(gamepad2.left_stick_y);

        Cranemotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Cranemotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (gamepad2.x) {
            Cranemotor.setTargetPosition(pulse);
        }
        if (gamepad2.y) {
            Cranemotor.setTargetPosition(-1*pulse);
        }
        if (gamepad2.right_stick_button) {
            Cranemotor.setPower(0);
        }
       while (gamepad2.left_trigger > 0) {
            Cranemotor.setPower(-0.5);
       }
        while (gamepad2.right_trigger > 0) {
            Cranemotor.setPower(0.5);
        }
//        if (gamepad2.y) {
//            Cranemotor.setPower(0);
//        }
        if (gamepad2.right_bumper){
            Crane1.setPower(0.5);
        }
        if (gamepad2.left_bumper) {
            Crane1.setPower(-0.5);
        }

     //   Arm.setPower(gamepad2.left_stick_y);


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

        if (gamepad1.x)       {
            LF.setPower(1);
            LB.setPower(1);
            RF.setPower(1);
            RB.setPower(1);
        }


        if (gamepad1.y) {

            LF.setPower(1);
            LB.setPower(1);
            RF.setPower(1);
            RB.setPower(1);
            ElapsedTime elapsedTime = new ElapsedTime();
            FI.setPower(-.5);
            elapsedTime.reset();

            while (elapsedTime.seconds() < 1){

            }
        }
        if (gamepad2.b) {
            ElapsedTime elapsedTime = new ElapsedTime();
            FI.setPower(.5);
            elapsedTime.reset();

            while (elapsedTime.seconds() < .5){

            }
            FI.setPower(0);
        }

        if (gamepad2.a) {
            ElapsedTime elapsedTime = new ElapsedTime();
            FI.setPower(-.5);
            elapsedTime.reset();

            while (elapsedTime.seconds() < .5){

            }
            FI.setPower(0);
        }

}}
