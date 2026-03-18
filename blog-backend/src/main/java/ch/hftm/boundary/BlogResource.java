package ch.hftm.boundary;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import ch.hftm.control.AttachmentService;
import ch.hftm.control.BlogService;
import ch.hftm.entity.Blog;
import ch.hftm.boundary.dto.StorageInfo;
import ch.hftm.messaging.ValidationRequest;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@Path("/blogs")
public class BlogResource {

    @Inject
    BlogService blogService;

    @Inject
    AttachmentService attachmentService;

    @Inject
    @Channel("validation-request")
    Emitter<ValidationRequest> validationRequestEmitter;

    @GET
    public List<Blog> getBlogs(@QueryParam("status") String status) {
        Log.info("GET /blogs" + (status != null ? "?status=" + status : ""));
        if (status != null) {
            return blogService.getBlogsByStatus(status);
        }
        return blogService.getBlogs();
    }

    @GET
    @Path("{id}")
    public Blog getBlog(@PathParam("id") long id) {
        Log.info("GET /blogs/" + id);
        return blogService.getBlog(id);
    }

    @POST
    public Response addBlog(@Valid Blog blog) {
        Log.info("POST /blogs - " + blog.getTitle());
        blogService.addBlog(blog);

        validationRequestEmitter.send(
            new ValidationRequest(blog.getId(), "BLOG", blog.getTitle() + " " + blog.getContent())
        );
        Log.info("Validation request sent for blog id=" + blog.getId());
        return Response.status(Response.Status.CREATED).entity(blog).build();
    }

    @GET
    @Path("{id}/storage")
    public StorageInfo getStorageInfo(@PathParam("id") long id) {
        Log.info("GET /blogs/" + id + "/storage");
        return attachmentService.getStorageInfo(id);
    }

    @DELETE
    @Path("{id}")
    public Response deleteBlog(@PathParam("id") long id) {
        Log.info("DELETE /blogs/" + id);
        blogService.deleteBlog(id);
        return Response.noContent().build();
    }
}
