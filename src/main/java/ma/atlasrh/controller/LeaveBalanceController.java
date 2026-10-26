
package ma.atlasrh.controller;

import ma.atlasrh.dto.LeaveBalanceDTO;
import ma.atlasrh.service.LeaveBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(
            LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam int year) {

        LeaveBalanceDTO leaveBalance =
                leaveBalanceService.getLeaveBalance(
                        employeeId,
                        leaveTypeId,
                        year
                );

        return ResponseEntity.ok(leaveBalance);
    }
}
