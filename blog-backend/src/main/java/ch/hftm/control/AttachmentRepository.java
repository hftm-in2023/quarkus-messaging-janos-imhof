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

    public long getTotalStorageByBlogId(Long blogId) {
        Long total = getEntityManager()
                .createQuery("SELECT COALESCE(SUM(a.fileSize), 0) FROM Attachment a WHERE a.blog.id = :blogId",
                        Long.class)
                .setParameter("blogId", blogId)
                .getSingleResult();
        return total;
    }
}
