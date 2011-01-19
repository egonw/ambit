package ambit2.search.chemidplus;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.exceptions.HttpException;


public abstract class AbstractSearchRequest<R> extends DefaultAmbitProcessor<String, R> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5629570446572417364L;

	protected abstract R read(InputStream in) throws Exception;
	
	public R get(URL url) throws AmbitException {

		InputStream in = null;
		HttpURLConnection uc = null;
		try {
			uc =(HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("GET");
			int code = uc.getResponseCode();
			
			if (code==200) {
				in= uc.getInputStream();
				return read(uc.getInputStream());
			} else throw new HttpException(url.toString(),code,uc.getResponseMessage());	
		} catch (HttpException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception x) { 
			}
		}
	}
}
