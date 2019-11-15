import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorConstantPowerCommand;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class testst extends OpMode {
    private DcMotor RF,RB,LF,LB,FI;
    @Override
    public void init() {
        RF = hardwareMap.dcMotor.get("RFM"); //gets RFM on hardware map
        RB = hardwareMap.dcMotor.get("RBM"); //gets RBM on hardware map
        LF = hardwareMap.dcMotor.get("LFM"); //gets LFM on hardware map
        LB = hardwareMap.dcMotor.get("LBM"); //gets LBM on hardware map
        FI = hardwareMap.dcMotor.get("FI"); //gets Front Intake on hardware map

        LF.setDirection(DcMotor.Direction.REVERSE); //sets both left side motors on reverse
        LB.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void loop() {

        RF.setPower(-(gamepad1.right_stick_y)); //establishes basic tank drive controls
        RB.setPower(-(gamepad1.right_stick_y));
        LF.setPower(-(gamepad1.left_stick_y));
        LB.setPower(-(gamepad1.left_stick_y));

        if (gamepad1.b) {
            ElapsedTime elapsedTime = new ElapsedTime();
            FI.setPower(.5);
            elapsedTime.reset();

            while (elapsedTime.seconds() < 1){

            }
        }

}}
