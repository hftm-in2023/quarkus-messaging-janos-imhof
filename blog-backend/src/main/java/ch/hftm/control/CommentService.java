package ch.hftm.control;

import java.time.LocalDateTime;
import java.util.List;

import ch.hftm.entity.Blog;
import ch.hftm.entity.Comment;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CommentService {

    @Inject
    CommentRepository commentRepository;

    @Inject
    BlogRepository blogRepository;

    public List<Comment> getCommentsByBlogId(long blogId) {
        Log.info("Getting comments for blog id=" + blogId);
        return commentRepository.list("blog.id", blogId);
    }

    @Transactional
    public Comment addComment(long blogId, Comment comment) {
        Blog blog = blogRepository.findById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("Blog mit ID " + blogId + " nicht gefunden.");
        }
        comment.setBlog(blog);
        comment.setValidationStatus("PENDING");
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.persist(comment);
        Log.info("Added comment id=" + comment.getId() + " to blog id=" + blogId);
        return comment;
    }
}
