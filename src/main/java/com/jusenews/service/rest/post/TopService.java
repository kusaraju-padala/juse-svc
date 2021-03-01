package com.jusenews.service.rest.post;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.jusenews.service.rest.verifyToken.VerifyToken;
import com.top.lib.beans.generic.InsertResponseBean;
import com.top.lib.beans.post.ImageOutputBean;
import com.top.lib.beans.post.PostBean;
import com.top.lib.beans.thought.ThoughtBean;
import com.top.lib.beans.vote.ThoughtVoteBean;
import com.top.lib.post.image.ImageProcess;
import com.top.lib.post.post.AddPost;
import com.top.lib.post.thought.AddThought;
import com.top.lib.post.thought.DeleteThought;
import com.top.lib.post.thought.UpdateThought;
import com.top.lib.post.vote.DownvoteaThought;
import com.top.lib.post.vote.UnDownvoteaThought;
import com.top.lib.post.vote.UnUpvoteaThought;
import com.top.lib.post.vote.UpvoteaThought;

import io.jsonwebtoken.Claims;

@Path("/in")
public class TopService {

	private static final Logger logger = LogManager.getLogger(TopService.class);

	private static final String UPLOAD_IMAGE_KEY = "image";
	private static final String UPLOAD_IMAGETYPE_KEY = "imagetype";
	//private static final String UPLOAD_POST_KEY = "data";

	private static final String POST_UPLOAD_TAGS_KEY = "topics";
	private static final String POST_UPLOAD_LANGUAGE_KEY = "languageid";
	private static final String POST_UPLOAD_POST_TYPE_KEY = "posttype";
	private static final String POST_UPLOAD_OWNER_ID_KEY = "ownerid";
	private static final String POST_UPLOAD_SOURCE_KEY = "source";

	public static final int THUMBNAIL_WIDTH = 200;
	public static final int WIDE_IMAGE_WIDTH = 400;
	public static final int FULL_IMAGE_WIDTH = 600;
	public static final int TOPIC_IMAGE_WIDTH = 200;

	public static final String POST_TYPE_THUMBNAIL = "thumbnail";
	public static final String POST_TYPE__HALF_WIDE = "halfwide";
	public static final String POST_TYPE_FULL = "fullimage";
	public static final String POST_TYPE_NO_IMAGE = "noimage";
	public static final String POST_TYPE_POLL = "poll";

	public static final String DEFAULT_THOUGHT_SOURCE_FORMAT = "default";

	@SuppressWarnings("serial")
	public static final Map<String, Integer> WIDTH_MAP = new HashMap<String, Integer>() {
		{
			put(POST_TYPE_THUMBNAIL, THUMBNAIL_WIDTH);
			put(POST_TYPE__HALF_WIDE, WIDE_IMAGE_WIDTH);
			put(POST_TYPE_FULL, FULL_IMAGE_WIDTH);
		}
	};

	@GET
	@Path("/test")
	public Response getUser(String json) {
		String u = "<h3>Keep going</h3>";
		return Response.ok(u).build();
	}

	@POST
	@Path("/addpost")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json")
	public Response addPost(MultipartFormDataInput multipartFormDataInput, @Context HttpHeaders headers) {
		InsertResponseBean response = new InsertResponseBean();
		try {

			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
			logger.debug("Size of map" + map.size());
			
			
			// String data = new String(getBytesFromMultipartMap(map, UPLOAD_POST_KEY));
			PostBean post = new PostBean();
			post.setTopics(new String(getBytesFromMultipartMap(map, POST_UPLOAD_TAGS_KEY)));
			post.setPostType(new String(getBytesFromMultipartMap(map, POST_UPLOAD_POST_TYPE_KEY)));
			post.setLanguageId(Integer.parseInt(new String(getBytesFromMultipartMap(map, POST_UPLOAD_LANGUAGE_KEY))));
			post.setOwnerId(Integer.parseInt(new String(getBytesFromMultipartMap(map, POST_UPLOAD_OWNER_ID_KEY))));
			post.setSourceUrl(new String(getBytesFromMultipartMap(map, POST_UPLOAD_SOURCE_KEY)));
			post.setStateId(0);
			post.setCountryId(1);
			post.setHeading(
					(new String(getBytesFromMultipartMap(map, "heading"), StandardCharsets.UTF_8)).replace("'", "''"));
			byte[] body = getBytesFromMultipartMap(map, "body");
			if (body != null) {
				String fullbody = (new String(getBytesFromMultipartMap(map, "body"), StandardCharsets.UTF_8))
						.replace("'", "''");
				post.setBody(fullbody);
			}
			if(!post.getPostType().equalsIgnoreCase("noimage")) {
				String imageType = new String(getBytesFromMultipartMap(map, UPLOAD_IMAGETYPE_KEY));
				byte[] imageBytes = getBytesFromMultipartMap(map, UPLOAD_IMAGE_KEY);
				ImageOutputBean img = new ImageProcess().addImage(imageBytes, imageType, post.getOwnerId(),
						getWidth(post.getPostType()));
				post.setImage(img);
			}
			
			response.setKey(new AddPost().addPost(post));
			response.setCode(0);
			response.setMessage("Successfully added post");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();
	}

	private int getWidth(String type) {
		return WIDTH_MAP.get(type);
	}

	@POST
	@Path("/joarecaluraewhoiuhohalsjdflhasldfhblj")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json")
	public Response addImageToOurGcp(MultipartFormDataInput multipartFormDataInput) {
		try {

			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();

			byte[] imageBytes = getBytesFromMultipartMap(map, UPLOAD_IMAGE_KEY);
			String imageType = new String(getBytesFromMultipartMap(map, UPLOAD_IMAGETYPE_KEY));
			ImageOutputBean img = new ImageProcess().addImage(imageBytes, imageType, 100000, WIDE_IMAGE_WIDTH);

			return Response.ok(img).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity("Internal Server Error").build();
		}

	}

	private byte[] getBytesFromMultipartMap(Map<String, List<InputPart>> map, String key) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			List<InputPart> lstInputPart = map.get(key);
			if (null != lstInputPart) {
				for (InputPart inputPart : lstInputPart) {
					buffer.write(inputPart.getBody(InputStream.class, null));
				}
				bytes = buffer.toByteArray();
				buffer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bytes;
	}

	@POST
	@VerifyToken
	@Path("/thought")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json; charset=UTF-8")
	public Response addThought(MultipartFormDataInput multipartFormDataInput) {
		InsertResponseBean response = new InsertResponseBean();
		try {
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();

			ThoughtBean thought = new ThoughtBean();
			thought.setPreviewcontent(
					(new String(getBytesFromMultipartMap(map, "previewcontent"), StandardCharsets.UTF_8)).replace("'",
							"''"));
			if (null != getBytesFromMultipartMap(map, "previewimage")
					&& null != getBytesFromMultipartMap(map, "objectids")) {
				thought.setPreviewimage(
						(new String(getBytesFromMultipartMap(map, "previewimage"), StandardCharsets.UTF_8)).replace("'",
								"''"));
				thought.setObjectids(
						(new String(getBytesFromMultipartMap(map, "objectids"), StandardCharsets.UTF_8)).split(","));
			}
			if (null != getBytesFromMultipartMap(map, "sourceFormat"))
				thought.setSourceFormat(
						new String(getBytesFromMultipartMap(map, "sourceFormat"), StandardCharsets.UTF_8));
			else
				thought.setSourceFormat(DEFAULT_THOUGHT_SOURCE_FORMAT);
			thought.setPostId(Integer.parseInt(new String(getBytesFromMultipartMap(map, "postid"))));
			byte[] fullcontent = getBytesFromMultipartMap(map, "fullcontent");
			if (fullcontent != null) {
				thought.setFullcontent((new String(fullcontent, StandardCharsets.UTF_8)).replace("'", "''"));
			}
			thought.setUserId((Integer) (claims.get("id")));

			response.setData(new AddThought().addThought(thought));
			response.setCode(0);
			response.setMessage("Successfully added thought");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();

	}

	@PUT
	@VerifyToken
	@Path("/thought")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json; charset=UTF-8")
	public Response updateThought(MultipartFormDataInput multipartFormDataInput) {
		InsertResponseBean response = new InsertResponseBean();
		try {
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();

			ThoughtBean thought = new ThoughtBean();
			thought.setPreviewcontent(
					(new String(getBytesFromMultipartMap(map, "previewcontent"), StandardCharsets.UTF_8)).replace("'",
							"''"));
			if (null != getBytesFromMultipartMap(map, "previewimage")
					&& null != getBytesFromMultipartMap(map, "objectids")) {
				thought.setPreviewimage(
						(new String(getBytesFromMultipartMap(map, "previewimage"), StandardCharsets.UTF_8)).replace("'",
								"''"));
				thought.setObjectids(
						(new String(getBytesFromMultipartMap(map, "objectids"), StandardCharsets.UTF_8)).split(","));
			}
			if (null != getBytesFromMultipartMap(map, "thoughtid")) {
				thought.setId(Integer
						.parseInt(new String(getBytesFromMultipartMap(map, "thoughtid"), StandardCharsets.UTF_8)));
			}
			if (null != getBytesFromMultipartMap(map, "sourceFormat"))
				thought.setSourceFormat(
						new String(getBytesFromMultipartMap(map, "sourceFormat"), StandardCharsets.UTF_8));

			thought.setPostId(Integer.parseInt(new String(getBytesFromMultipartMap(map, "postid"))));
			byte[] fullcontent = getBytesFromMultipartMap(map, "fullcontent");
			if (fullcontent != null) {
				thought.setFullcontent((new String(fullcontent, StandardCharsets.UTF_8)).replace("'", "''"));
			}
			thought.setUserId((Integer) (claims.get("id")));
			response.setData(new UpdateThought().updateThought(thought));
			response.setCode(0);
			response.setMessage("Successfully updated thought");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();

	}

	@DELETE
	@VerifyToken
	@Path("/thought")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json; charset=UTF-8")
	public Response deleteThought(MultipartFormDataInput multipartFormDataInput) {
		InsertResponseBean response = new InsertResponseBean();
		try {
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();

			ThoughtBean thought = new ThoughtBean();

			if (null != getBytesFromMultipartMap(map, "thoughtid"))
				thought.setId(Integer
						.parseInt((new String(getBytesFromMultipartMap(map, "thoughtid"), StandardCharsets.UTF_8))));
			else {
				throw new Exception("Thought id is null please provide post id");
			}

//			if (null != getBytesFromMultipartMap(map, "postid"))
//				thought.setPostId(Integer
//						.parseInt((new String(getBytesFromMultipartMap(map, "postid"), StandardCharsets.UTF_8))));
//			else {
//				throw new Exception("Post id is null please provide post id");
//			}
			thought.setUserId((Integer) (claims.get("id")));
			response.setKey(new DeleteThought().deleteThought(thought));
			response.setCode(0);
			response.setMessage("Successfully deleted thought");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();
	}

	@VerifyToken
	@POST
	@Path("/upvoteathought/{thoughtid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response upvoteaThought(@PathParam("thoughtid") Integer thoughtId) {
		InsertResponseBean response = new InsertResponseBean();
		ThoughtVoteBean tvb = new ThoughtVoteBean();
		tvb.setThoughtId(thoughtId);
		tvb.setUserId((Integer) ResteasyProviderFactory.popContextData(Claims.class).get("id"));
		try {
			response.setKey(new UpvoteaThought().upvoteThought(tvb));
			response.setCode(0);
			response.setMessage("Successfully upvoted");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();
	}

	@VerifyToken
	@POST
	@Path("/downvoteathought/{thoughtid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response downvoteaThought(@PathParam("thoughtid") Integer thoughtId) {
		InsertResponseBean response = new InsertResponseBean();
		ThoughtVoteBean tvb = new ThoughtVoteBean();
		tvb.setThoughtId(thoughtId);
		tvb.setUserId((Integer) ResteasyProviderFactory.popContextData(Claims.class).get("id"));
		try {
			response.setKey(new DownvoteaThought().downvoteThought(tvb));
			response.setCode(0);
			response.setMessage("Successfully downvoted");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();
	}

	@VerifyToken
	@POST
	@Path("/unupvoteathought/{thoughtid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response unvoteaThought(@PathParam("thoughtid") Integer thoughtId) {
		InsertResponseBean response = new InsertResponseBean();
		ThoughtVoteBean tvb = new ThoughtVoteBean();
		tvb.setThoughtId(thoughtId);
		tvb.setUserId((Integer) ResteasyProviderFactory.popContextData(Claims.class).get("id"));
		try {
			response.setKey(new UnUpvoteaThought().unUpvoteThought(tvb));
			response.setCode(0);
			response.setMessage("Successfully unupvoted");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();
	}

	@POST
	@VerifyToken
	@Path("/undownvoteathought/{thoughtid}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response undownvoteaThought(@PathParam("thoughtid") Integer thoughtId) {
		InsertResponseBean response = new InsertResponseBean();
		ThoughtVoteBean tvb = new ThoughtVoteBean();
		tvb.setThoughtId(thoughtId);
		tvb.setUserId((Integer) ResteasyProviderFactory.popContextData(Claims.class).get("id"));
		try {
			response.setKey(new UnDownvoteaThought().unDownvoteThought(tvb));
			response.setCode(0);
			response.setMessage("Successfully undownvoted");
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();
	}

	// Krishna
	// This is looking good, looking forward to proceed, bless yourself

	/*
	 * 1. Post a thought 2. Upvote a thought 3. Downvote a thought 4. Getupvotes for
	 * a thought 5. Getdownvotes for a thought 6. Get thoughts for a post 7. Delete
	 * the thought 8. Edit the thought 9. All thoughts posted by a user 10. First we
	 * will give list of posts suitable for the user as user starts reading we will
	 * get posts one after the other
	 * 
	 */

}
