package com.thinkalike.taskmanager.repository;

import com.thinkalike.taskmanager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // all comments on a specific task ordered by creation time
    // Spring parses "OrderByCreatedAtAsc" from the method name
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    // all comments written by a specific user
    List<Comment> findByAuthorId(Long authorId);

}
