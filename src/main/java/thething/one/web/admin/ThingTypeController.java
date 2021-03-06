package thething.one.web.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import thething.exceptions.DatabaseException;
import thething.one.dataobjects.FieldDescriptor;
import thething.one.dataobjects.Thing;
import thething.one.dataobjects.ThingType;
import thething.one.db.utils.RequestWrapper;
import thething.one.db.utils.ThingFilter;
import thething.one.web.BaseController;
import thething.utils.ThingTools;

@Controller
@RequestMapping("/thingtype")
public class ThingTypeController extends BaseController {

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void createThing(HttpServletRequest request, HttpServletResponse response) throws IOException, DatabaseException {
		logger.info(this.printRequestparams(request.getParameterMap()));
		ThingType type = requestToType(request);
		thingDao.createOrUpdateWithRelations(typeDescriptorService.thingFromDescriptor(type));
		response.getWriter().write("it works");
	}
	
	@RequestMapping(value = "/json/create", method = RequestMethod.POST)
	@ResponseBody
	public Object createThing(@RequestBody Map<String, Object> request) throws DatabaseException {
		return this.createOrUpdateThing(request);
	}

	@ResponseBody
	@RequestMapping(value = "/get/json/label/{label}", method = RequestMethod.GET)
	public Object getJsonByLabel(@PathVariable("label") String label,
								HttpServletRequest request, 
								HttpServletResponse response) throws DatabaseException {
		ThingFilter filter = new ThingFilter();
		filter.setLabel(label);
		return createJsonResponse(request, response, filter);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/json/{property}/{value}", method = RequestMethod.GET)
	public Object getJsonById(@PathVariable("property") String property, 
							@PathVariable("value") String value,
							HttpServletRequest request, 
							HttpServletResponse response) throws DatabaseException {
		ThingFilter filter = new ThingFilter();
		filter.setProperty(property, value);
		return createJsonResponse(request, response, filter);
		
	}
	
	
	@SubscribeMapping("/label/{label}")
	public Object wsGetJsonByLabel(@DestinationVariable String label) throws DatabaseException {
		ThingFilter filter = new ThingFilter();
		filter.setLabel(label);
		return processJsonRequest2(filter, "resultWrapper");
	}
	private Object processJsonRequest2(ThingFilter filter, String responseFormat) throws DatabaseException {
		if (responseFormat == null) {
			responseFormat = "thing";
		}
		Object responseObject = null;
		switch (responseFormat) {
		case "cyto" :
			responseObject = thingDao.getCytoWrapperByFilter(filter);
			break;
		case "resultWrapper" :
			responseObject = thingDao.getResultsByFilter(filter);
			break;
		case "thing" :
			responseObject = thingDao.getThingByFilter(filter);
			break;
			
		default :
			logger.warn("Illegal responseFormat queried");
		}
		return responseObject;
	}
	private void writeThing(HttpServletResponse response, Thing thing) {
		try {
			logger.info(mapper.writeValueAsString(thing));
			mapper.writeValue(response.getOutputStream(), thing);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	private ThingType requestToType(HttpServletRequest servletRequest ) {
		RequestWrapper request = new RequestWrapper(servletRequest);
		Class<?>[] fieldTypes = request.getClassValues("fieldType");
		boolean[] fieldRequired = request.getBooleanValues("required");
		String[] fieldDisplayNames = request.getParameterValues("fieldDisplayName");
		String[] ids = request.getParameterValues("id");
		String[] fieldNames = request.getParameterValues("fieldName");
		//Ids should actually always exist. Currently here as a hack to be removed
		if (ids == null) {
			ids = new String[fieldNames.length];
		}
		
		ThingType type = new ThingType();
		type.setTypeName(request.getParameter("typeName"));
		type.setId(request.getParameter("typeId"));
		for (int i = 0; i < fieldNames.length; i++) {
			FieldDescriptor field = new FieldDescriptor();
			field.setName(fieldNames[i]);
			field.setType(fieldTypes[i]);
			field.setDisplayName(fieldDisplayNames[i]);
			field.setRequired(fieldRequired[i]);
			field.setId(ids[i]);
			type.addField(field);
		}
		logger.info(type);
		
		return type;
	}
	
	
	
}
