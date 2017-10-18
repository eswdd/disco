/*
 * Copyright 2014, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.exemel.disco.transformations;

import uk.co.exemel.disco.codegen.DocumentMangler;
import uk.co.exemel.disco.codegen.IDLReader;
import uk.co.exemel.disco.codegen.Service;
import uk.co.exemel.disco.codegen.Transformations;
import uk.co.exemel.disco.codegen.Validator;
import uk.co.exemel.disco.codegen.XmlUtil;
import uk.co.exemel.disco.codegen.resolver.InterceptingResolver;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.w3c.dom.Document;

import java.io.File;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Transformations transform = new DiscoTransformations();
		IDLReader reader = new IDLReader();
		Log log = new SystemStreamLog();

		File idd = new File("src\\main\\resources\\BaselineService.xml");
		InterceptingResolver resolver = new InterceptingResolver();

		Document iddDoc = XmlUtil.parse(idd, resolver);

		File ext = new File("src\\main\\resources\\BaselineService-Extensions.xml");
		Document extDoc = null;
		if (ext.exists()) {
			extDoc = XmlUtil.parse(ext, resolver);
		}

		reader.init(iddDoc, extDoc, "BaselineService",
						"com.betfair.baseline", ".", "/target/generated-sources", log,
						new Service().getOutputDir(), true, true);

        // First let's mangle the document if need be.
        if (transform.getManglers() != null) {
        	log.debug("mangling IDL using "+transform.getManglers().size()+" pre validations");
        	for(DocumentMangler m : transform.getManglers()) {
        		log.debug(m.getName());
           	 	reader.mangle(m);
            }
    		log.debug(reader.serialize());
        }

		for (Validator v: transform.getPreValidations()) {
			reader.validate(v);
		}
		log.debug(reader.serialize());
		reader.runMerge(transform.getTransformations());

		reader.writeResult();

	}

}
