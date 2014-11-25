package thething.kodurestoranid.web.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thething.kodurestoranid.dataobjects.FieldDescriptor;
import thething.kodurestoranid.dataobjects.ThingType;
import thething.kodurestoranid.db.utils.RequestWrapper;
import thething.kodurestoranid.db.utils.Tools;
import thething.kodurestoranid.web.BaseController;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController{

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void createThing(HttpServletRequest request, HttpServletResponse response) throws IOException{
		logger.info(this.printRequestparams(request.getParameterMap()));
		ThingType type = requestToType(request);
		thingDao.createOrUpdateWithRelations(typeDescriptorService.thingFromDescriptor(type));
		response.getWriter().write("it works");
	}
	
	
	
	private ThingType requestToType(HttpServletRequest servletRequest){
		RequestWrapper request = new RequestWrapper(servletRequest);
		Class<?>[] fieldTypes = request.getClassValues("fieldType");
		boolean[] fieldRequired = request.getBooleanValues("required");
		String[] fieldDisplayNames = request.getParameterValues("fieldDisplayName");
		String[] ids = request.getParameterValues("id");
		String[] fieldNames = request.getParameterValues("fieldName");
		//Ids should actually always exist. Currently here as a hack to be removed
		if(ids == null){
			ids = new String[fieldNames.length];
		}
		
		ThingType type = new ThingType();
		type.setTypeName(request.getParameter("typeName"));
		type.setId(request.getParameter("typeId"));
		for(int i = 0; i < fieldNames.length; i++){
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
