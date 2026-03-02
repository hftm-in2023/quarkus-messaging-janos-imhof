package ch.hftm.boundary;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import ch.hftm.control.BlogService;
import ch.hftm.control.CommentService;
import ch.hftm.entity.Blog;
import ch.hftm.entity.Comment;
import ch.hftm.messaging.CommentEvent;
import ch.hftm.messaging.ValidationRequest;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@Path("/blogs/{blogId}/comments")
public class CommentResource {

    @Inject
    CommentService commentService;

    @Inject
    BlogService blogService;

    @Inject
    @Channel("validation-request")
    Emitter<ValidationRequest> validationRequestEmitter;

    @Inject
    @Channel("comment-events")
    Emitter<CommentEvent> commentEventEmitter;

    @GET
    public List<Comment> getComments(@PathParam("blogId") long blogId) {
        Log.info("GET /blogs/" + blogId + "/comments");
        return commentService.getCommentsByBlogId(blogId);
    }

    @POST
    public Response addComment(@PathParam("blogId") long blogId, @Valid Comment comment) {
        Log.info("POST /blogs/" + blogId + "/comments - " + comment.getAuthor());
        Comment saved = commentService.addComment(blogId, comment);

        Blog blog = blogService.getBlog(blogId);

        validationRequestEmitter.send(
            new ValidationRequest(saved.getId(), "COMMENT", saved.getContent())
        );
        Log.info("Validation request sent for comment id=" + saved.getId());

        commentEventEmitter.send(
            new CommentEvent(blogId, blog.getTitle(), saved.getAuthor(), saved.getContent())
        );
        Log.info("Comment event sent for blog id=" + blogId);

        return Response.status(Response.Status.CREATED).entity(saved).build();
    }
}
