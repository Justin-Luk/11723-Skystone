import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class shiftleft extends LinearOpMode {
    public DcMotor RF, RB, LF, LB, FI;
    @Override
    public void runOpMode() throws InterruptedException {
        RF = hardwareMap.dcMotor.get("RF"); //gets RFM on hardware map
        RB = hardwareMap.dcMotor.get("RB"); //gets RBM on hardware map
        LF = hardwareMap.dcMotor.get("LF"); //gets LFM on hardware map
        LB = hardwareMap.dcMotor.get("LB"); //gets LBM on hardware map


        waitForStart();
        if (opModeIsActive()) {
            LF.setPower(1);
            LB.setPower(1);
            RF.setPower(1);
            RB.setPower(1);
            sleep(500);

            LF.setPower(1);
            LB.setPower(-1);
            RF.setPower(1);
            RB.setPower(-1);
            sleep(200);
        }
    }
}
