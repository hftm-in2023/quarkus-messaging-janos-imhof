package ch.hftm.control;

import java.time.LocalDateTime;
import java.util.List;

import ch.hftm.entity.Blog;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BlogService {

    @Inject
    BlogRepository blogRepository;

    public List<Blog> getBlogs() {
        Log.info("Getting all blogs");
        return blogRepository.listAll();
    }

    public Blog getBlog(long id) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new IllegalArgumentException("Blog mit ID " + id + " nicht gefunden.");
        }
        return blog;
    }

    @Transactional
    public void addBlog(Blog blog) {
        Log.info("Adding blog: " + blog.getTitle());
        blog.setCreatedAt(LocalDateTime.now());
        blogRepository.persist(blog);
    }

    @Transactional
    public void deleteBlog(long id) {
        Log.info("Deleting blog with id " + id);
        blogRepository.deleteById(id);
    }
}
