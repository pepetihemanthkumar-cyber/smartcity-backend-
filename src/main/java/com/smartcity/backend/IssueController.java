package com.smartcity.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // 🔥 IMPORTANT

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    private final IssueRepository repo;

    public IssueController(IssueRepository repo) {
        this.repo = repo;
    }

    /* ================= CREATE ================= */

    // ✅ SAVE ISSUE (USER + ADMIN)
    @PostMapping
    public Issue saveIssue(@RequestBody Issue issue) {
        return repo.save(issue);
    }

    /* ================= READ ================= */

    // ✅ GET ALL ISSUES (USER + ADMIN)
    @GetMapping
    public List<Issue> getAll() {
        return repo.findAll();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public Issue getById(@PathVariable Long id) {
        Optional<Issue> issue = repo.findById(id);
        return issue.orElse(null);
    }

    /* ================= UPDATE ================= */

    // ❌ OPTIONAL (you can restrict if needed)
    @PutMapping("/{id}")
    public Issue updateIssue(@PathVariable Long id, @RequestBody Issue updatedIssue) {

        Optional<Issue> optional = repo.findById(id);

        if (optional.isPresent()) {
            Issue issue = optional.get();

            issue.setTitle(updatedIssue.getTitle());
            issue.setDescription(updatedIssue.getDescription());
            issue.setCategory(updatedIssue.getCategory());
            issue.setLocationName(updatedIssue.getLocationName());
            issue.setLat(updatedIssue.getLat());
            issue.setLng(updatedIssue.getLng());
            issue.setStatus(updatedIssue.getStatus());

            return repo.save(issue);
        }

        return null;
    }

    // 🔥 ADMIN ONLY → UPDATE STATUS
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public Issue updateStatus(@PathVariable Long id, @RequestBody Issue updatedIssue) {

        Optional<Issue> optional = repo.findById(id);

        if (optional.isPresent()) {
            Issue issue = optional.get();
            issue.setStatus(updatedIssue.getStatus());
            return repo.save(issue);
        }

        return null;
    }

    /* ================= DELETE ================= */

    // 🔥 ADMIN ONLY → DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteIssue(@PathVariable Long id) {

        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Issue deleted successfully";
        }

        return "Issue not found";
    }
}