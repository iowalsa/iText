/*
 * $Id: CertificateVerifier.java 5465 2012-10-07 12:37:23Z blowagie $
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2012 1T3XT BVBA
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY 1T3XT,
 * 1T3XT DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.text.pdf.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Superclass for a series of certificate verifiers that will typically
 * be used in a chain. It wraps another <code>CertificateVerifier</code>
 * that is the next element in the chain of which the <code>verify()</code>
 * method will be called.
 */
public class CertificateVerifier {

	/** The previous CertificateVerifier in the chain of verifiers. */
	protected CertificateVerifier verifier;
	
	/** Indicates if going online to verify a certificate is allowed. */
	protected boolean onlineCheckingAllowed = true;

	/**
	 * Creates the final CertificateVerifier in a chain of verifiers.
	 * @param verifier	the previous verifier in the chain
	 */
	public CertificateVerifier(CertificateVerifier verifier) {
		this.verifier = verifier;
	}

	/**
	 * Decide whether or not online checking is allowed.
	 * @param onlineCheckingAllowed
	 */
	public void setOnlineCheckingAllowed(boolean onlineCheckingAllowed) {
		this.onlineCheckingAllowed = onlineCheckingAllowed;
	}
	
	/**
	 * Checks the validity of the certificate, and calls the next
	 * verifier in the chain, if any.
	 * @param signCert	the certificate that needs to be checked
	 * @param issuerCert	its issuer
	 * @param signDate		the date the certificate needs to be valid
	 * @return a list of <code>VerificationOK</code> objects.
	 * The list will be empty if the certificate couldn't be verified.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
			throws GeneralSecurityException, IOException {
		// Check if the certificate is valid on the signDate
		if (signDate != null)
			signCert.checkValidity(signDate);
		// Check if the signature is valid
		if (issuerCert != null) {
			signCert.verify(issuerCert.getPublicKey());
		}
		// Also in case, the certificate is self-signed
		else {
			signCert.verify(signCert.getPublicKey());
		}
		List<VerificationOK> result = new ArrayList<VerificationOK>();
		if (verifier != null)
			result.addAll(verifier.verify(signCert, issuerCert, signDate));
		return result;
	}
}