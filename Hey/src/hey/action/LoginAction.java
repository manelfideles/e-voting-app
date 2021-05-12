/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;
import hey.model.HeyBean;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() throws RemoteException {
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !username.equals("")) {
			this.getHeyBean().setUsername(this.username);
			this.getHeyBean().setPassword(this.password);
			session.put("username", username);
			session.put("password", password);
			session.put("loggedin", true); // this marks the user as logged in
			if (this.getHeyBean().getUserMatchesPassword())
				return SUCCESS;
			else
				return LOGIN;
		}
		else
			return LOGIN;
	}
	
	public void setUsername(String username) { this.username = username; }

	public void setPassword(String password) { this.password = password; }
	
	public HeyBean getHeyBean() {
		if(!session.containsKey("heyBean"))
			this.setHeyBean(new HeyBean());
		return (HeyBean) session.get("heyBean");
	}

	public void setHeyBean(HeyBean heyBean) {
		this.session.put("heyBean", heyBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
