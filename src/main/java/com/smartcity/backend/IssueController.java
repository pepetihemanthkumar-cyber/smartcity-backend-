package com.smartcity.backend;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    private final IssueRepository repo;

    public IssueController(IssueRepository repo) {
        this.repo = repo;
    }

    // 🔥 SAVE ISSUE
    @PostMapping
    public Issue saveIssue(@RequestBody Issue issue) {
        return repo.save(issue);
    }

    // 🔥 GET ALL ISSUES
    @GetMapping
    public List<Issue> getAll() {
        return repo.findAll();
    }

    // 🔥 DELETE ISSUE
    @DeleteMapping("/{id}")
    public String deleteIssue(@PathVariable Long id) {
        repo.deleteById(id);
        return "Issue deleted successfully";
    }

    // 🔥 UPDATE STATUS
    @PutMapping("/{id}")
    public Issue updateStatus(@PathVariable Long id, @RequestBody Issue updatedIssue) {
        Issue issue = repo.findById(id).orElseThrow();

        issue.setStatus(updatedIssue.getStatus());

        return repo.save(issue);
    }
}