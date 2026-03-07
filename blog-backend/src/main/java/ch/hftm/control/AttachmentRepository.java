package ch.hftm.control;

import java.util.List;

import ch.hftm.entity.Attachment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AttachmentRepository implements PanacheRepository<Attachment> {

    public List<Attachment> findByBlogId(Long blogId) {
        return list("blog.id", blogId);
    }

    public void deleteByBlogId(Long blogId) {
        delete("blog.id", blogId);
    }
}
