import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class testst extends OpMode {
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
        Cranemotor = hardwareMap.dcMotor.get("Cranemotor");
        Arm = hardwareMap.servo.get("ARM");
        Crane1  = hardwareMap.crservo.get("Crane1");

        RB.setDirection(DcMotor.Direction.REVERSE); //sets both left side motors on reverse
        RF.setDirection(DcMotor.Direction.REVERSE);

        Arm.setPosition(1 );
       //Arm2.setPosition(.2);

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

        RF.setPower(-(gamepad1.right_stick_y)); //establishes basic tank drive controls
        RB.setPower(-(gamepad1.right_stick_y));
        LF.setPower(-(gamepad1.left_stick_y));
        LB.setPower(-(gamepad1.left_stick_y));

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

        while (gamepad1.right_stick_x > 0) {
            RF.setPower(1);
            LF.setPower(1);

        }
        while (gamepad1.right_stick_x < 0) {
            RF.setPower(-1);
            LF.setPower(-1);

        }
        while (gamepad1.left_stick_x > 0) {
            LB.setPower(1);
            RB.setPower(1);

        }
        while (gamepad1.left_stick_x < 0) {
            LB.setPower(-1);
            RB.setPower(-1);

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

        if (gamepad2.right_bumper){
            Arm.setPosition(1);
        }
        if (gamepad2.left_bumper) {
            Arm.setPosition(.6);
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

            while (elapsedTime.seconds() < 1){

            }
            FI.setPower(0);
        }

        if (gamepad2.a) {
            ElapsedTime elapsedTime = new ElapsedTime();
            FI.setPower(-.5);
            elapsedTime.reset();

            while (elapsedTime.seconds() < 1){

            }
            FI.setPower(0);
        }

}}
