package ch.hftm.control;

import java.time.LocalDateTime;
import java.util.List;

import ch.hftm.boundary.exception.ResourceNotFoundException;
import ch.hftm.entity.Blog;
import ch.hftm.entity.ValidationStatus;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BlogService {

    @Inject
    BlogRepository blogRepository;

    @Inject
    AttachmentService attachmentService;

    @Inject
    CommentRepository commentRepository;

    public List<Blog> getBlogs() {
        Log.info("Getting all blogs");
        return blogRepository.listAll();
    }

    public List<Blog> getBlogsByStatus(String status) {
        Log.info("Getting blogs with status=" + status);
        ValidationStatus validationStatus = ValidationStatus.valueOf(status.toUpperCase());
        return blogRepository.list("validationStatus", validationStatus);
    }

    public Blog getBlog(long id) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new ResourceNotFoundException("Blog with ID " + id + " not found.");
        }
        return blog;
    }

    @Transactional
    public void addBlog(Blog blog) {
        Log.info("Adding blog: " + blog.getTitle());
        blog.setValidationStatus(ValidationStatus.PENDING);
        blog.setCreatedAt(LocalDateTime.now());
        blogRepository.persist(blog);
    }

    @Transactional
    public void deleteBlog(long id) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new ResourceNotFoundException("Blog with ID " + id + " not found.");
        }
        Log.info("Deleting blog with id " + id);
        attachmentService.deleteAttachmentsByBlogId(id);
        commentRepository.delete("blog.id", id);
        blogRepository.getEntityManager().flush();
        blogRepository.getEntityManager().clear();
        blogRepository.deleteById(id);
    }
}
