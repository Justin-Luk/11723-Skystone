import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class exponential extends OpMode {
    private DcMotor RF,RB,LF,LB,FI,Cranemotor;
    private Servo Arm = null;
    private CRServo Crane1 = null;


    @Override
    public void init() {

        RF = hardwareMap.dcMotor.get("RF"); //gets RFM on hardware map
        RB = hardwareMap.dcMotor.get("RB"); //gets RBM on hardware map
        LF = hardwareMap.dcMotor.get("LF"); //gets LFM on hardware map
        LB = hardwareMap.dcMotor.get("LB"); //gets LBM on hardware map
        FI = hardwareMap.dcMotor.get("FI"); //gets Front Intake on hardware map
//        Cranemotor = hardwareMap.dcMotor.get("Cranemotor");
//        Arm = hardwareMap.servo.get("ARM");
//        Crane1  = hardwareMap.crservo.get("Crane1");

        RB.setDirection(DcMotor.Direction.REVERSE); //sets both left side motors on reverse
        LF.setDirection(DcMotor.Direction.REVERSE);
        LB.setDirection(DcMotor.Direction.REVERSE);

//        Arm.setPosition(1 );
       //Arm2.setPosition(.2);

        RF.setPower(0); //establishes basic tank drive controls
        RB.setPower(0);
        LF.setPower(0);
        LB.setPower(0);


    }

    @Override
    public void loop() {
        while (gamepad1.left_stick_y > 0.1) {
            double pl = Math.abs(gamepad1.left_stick_y);
            if (Math.abs(pl) < 0.1) pl = 0;
            double plf = Math.pow(pl,1.4);
            double plb = Math.pow(pl,1.4);
            double max = Math.max(1.0, Math.abs(plf));
            max = Math.max(max, Math.abs(plb));
            plf /= max;
            plb /= max;

            LF.setPower(plf);
            LB.setPower(plb);

            LF.setPower(0);
            LB.setPower(0);
        }

        while (gamepad1.left_stick_y < -0.1) {
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

            LF.setPower(0);
            LB.setPower(0);
        }
        while (gamepad1.right_stick_y > 0.1){
            double pr = Math.abs(gamepad1.right_stick_y);
            if (Math.abs(pr) < 0.05) pr = 0;

            double prf = Math.pow(pr,1.4);
            double prb = Math.pow(pr,1.4);
//                    max = Math.max(max, Math.abs(p3));
//                    max = Math.max(max, Math.abs(p4));
            RF.setPower(prf);
            RB.setPower(prb);

            RF.setPower(0);
            RB.setPower(0);
//                    p3 /= max;
//                    p4 /= max;
        }
        while (gamepad1.right_stick_y < -0.1){
            double pr = Math.abs(gamepad1.right_stick_y);
            if (Math.abs(pr) < 0.05) pr = 0;

            double prf = -1*Math.pow(pr,1.4);
            double prb = -1*Math.pow(pr,1.4);
//                    max = Math.max(max, Math.abs(p3));
//                    max = Math.max(max, Math.abs(p4));
            RF.setPower(prf);
            RB.setPower(prb);

            RF.setPower(0);
            RB.setPower(0);
//                    p3 /= max;
//                    p4 /= max;
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



}}
