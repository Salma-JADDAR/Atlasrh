package ma.atlasrh.repository;

import ma.atlasrh.entity.LeaveRequest;
import ma.atlasrh.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee_Id(Long employeeId);

    List<LeaveRequest> findByStatus(LeaveRequestStatus status);
}