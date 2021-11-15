package it.kdm.orchestratore.security;

import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.SOLRClient;
import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultEffectiveRights extends BaseEffectiveRights {

	@Override
	public long getEffectiveRights(Object target ,String token , String user)throws DocerApiException{
		
		if (!(target instanceof ICIFSObject))
		    return 2;

		ICIFSObject cObject = (ICIFSObject) target;
        SOLRClient client = new SOLRClient();

        String extendedAcl = cObject.getProperty(Security.EXTENDED_ACL_META_NAME);

        try {
            long rights = client.getAcl(token,cObject.getSolrId(),user);
            return remapValue(rights,extendedAcl);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }

		/*//Fascicolo
		if(target instanceof Fascicolo){
			rights = DocerService.recuperaDirittiEffettivi(token, (Fascicolo)target, user);
		}
		//Classifica
		if(target instanceof Titolario){
			rights = DocerService.recuperaDirittiEffettivi(token, (Titolario)target, user);
		}
		//Anagrafica
		if(target instanceof AnagraficaCustom){
			AnagraficaCustom anag = (AnagraficaCustom)target;
			rights = DocerService.recuperaDirittiEffettivi(token, anag, user);
		}
		//Documento
		if(target instanceof Documento){
			String docNum = ((Documento) target).getDocNum();
			rights = DocerService.recuperaDirittiEffettivi(token, docNum, user);
		}
		//Cartella
		if(target instanceof Cartella){
			rights = DocerService.recuperaDirittiEffettivi(token, (Cartella)target, user);
		}*/
		
		//return remapValue(rights,extendedAcl);
	}

	public long remapValue(long value, String extendedAcl){

        long result = 0;
		
		if(value == 0)
			result =  this.getRoleMask("full");
		else if(value == 1) 
			result =  this.getRoleMask("edit");
		else if(value == 2)
			result =  this.getRoleMask("read");
        else if(value == 3)
            result =  this.getRoleMask("view");
        else if(value == 4)
            result =  this.getRoleMask("create");





        if (extendedAcl!=null && !extendedAcl.equals("")) {
            try {
                extendedAcl = ";" + extendedAcl + ";";
                UserInfo uInfo = Session.getUserInfo();
                List<String> tokens = uInfo.getUserTokens();
                for (String tok : tokens) {
                    Pattern pattern = Pattern.compile(";"+tok+":([^\\;]+)\\;");
                    Matcher matcher = pattern.matcher(extendedAcl);
                    if (matcher.find()){
                        String role = matcher.group(1);
                        result = result |  this.getRoleMask(role);
                    }
                }
            }
            catch (Exception e) { return result;}
        }

		return result;
	}
	
}
