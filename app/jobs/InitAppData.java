package jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import flexjson.JSONDeserializer;
import models.City;
import models.Country;
import models.NormalUser;
import models.Role;
import models.State;
import models.Tag;
import models.User;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.vfs.VirtualFile;
import utils.CommonUtil;
import utils.FileReader;

@OnApplicationStart
public class InitAppData extends Job{
	
	@Override
	public void doJob() throws Exception {
		insertCountry();
		insertTags();
		createAdmin();
	}
	
	private void insertTags() {
		if(Tag.count() == 0){
			String path = Play.configuration.getProperty("assets.path") + "/tag.json";
			VirtualFile vf = VirtualFile.fromRelativePath(path);
			String json = FileReader.read(vf.inputstream());
			Tag.createTagsByJson(json);
		}
	}

	private void insertCountry(){
		if(Country.count() == 0){
			String path = Play.configuration.getProperty("assets.path") + "/country.json";
			VirtualFile vf = VirtualFile.fromRelativePath(path);
			String json = FileReader.read(vf.inputstream());
			Country.createCountriesByJson(json);
		}
	}
	
	private void createAdmin(){
		long count = User.count("byRoleAndIsDelete", Role.ADMIN, false);
		if(count < 1){
			NormalUser user = new NormalUser("admin@payee.com", "admin0payee");
			user.updateByRole(Role.ADMIN);
		}
	}
	
}
