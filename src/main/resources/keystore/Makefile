PASSWORD=changeit
KEYSTORE=keystore.jks
HOSTNAME=localhost
INTERNAME=intermediate
CLIENTNAME=cid

# CN = Common Name
# OU = Organization Unit
# O  = Organization Name
# L  = Locality Name
# ST = State Name
# C  = Country (2-letter Country Code)
# E  = Email
DNAME_CA='CN=KAharon CA,OU=ka.com,O=KAharon,L=SomeCity,ST=SomeState,C=CC'
DNAME_INTER='CN=KAharon Intermediate CA,OU=ka.com,O=KAharon,L=SomeCity,ST=SomeState,C=CC'
# For server certificates, the Common Name (CN) must be the hostname
DNAME_HOST='CN=$(HOSTNAME),OU=ka.com,O=KAharon,L=SomeCity,ST=SomeState,C=CC'
DNAME_CLIENT='CN=$(CLIENTNAME),OU=ka.com,O=KAharon,L=SomeCity,ST=SomeState,C=CC'
TRUSTSTORE=truststore.jks

all:	clean create-keystore add-intermediate add-host create-truststore add-client

create-keystore:
	# Generate a certificate authority (CA)
	keytool -genkey -alias ca -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_CA) \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	    
add-intermediate:
	# Generate an intermediate certificate
	keytool -genkey -alias $(INTERNAME) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_INTER) \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Generate an intermediate certificate signing request
	keytool -certreq -alias $(INTERNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(INTERNAME).csr" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Generate signed certificate with the certificate authority
	keytool -gencert -alias ca -ext BC=0 \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(INTERNAME).csr" -outfile "$(INTERNAME).crt" -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Import signed certificate into the keystore
	keytool -import -trustcacerts -alias $(INTERNAME) \
	    -file "$(INTERNAME).crt" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

add-host:
	# Generate a host certificate
	keytool -genkey -alias $(HOSTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_HOST) \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Generate a host certificate signing request
	keytool -certreq -alias $(HOSTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(HOSTNAME).csr" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Generate signed certificate with the certificate authority
	keytool -gencert -alias $(INTERNAME) \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(HOSTNAME).csr" -outfile "$(HOSTNAME).crt" -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Import signed certificate into the keystore
	keytool -import -trustcacerts -alias $(HOSTNAME) \
	    -file "$(HOSTNAME).crt" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

export-authority:
	# Export certificate authority
	keytool -export -alias ca -file ca.crt -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

create-truststore: export-authority
	# Import certificate authority into a new truststore
	keytool -import -trustcacerts -noprompt -alias ca -file ca.crt \
	    -keystore $(TRUSTSTORE) -storepass $(PASSWORD)
	# Import hostname certificate into truststore
	keytool -import -trustcacerts -noprompt -alias $(INTERNAME) -file $(INTERNAME).crt \
	    -keystore $(TRUSTSTORE) -storepass $(PASSWORD)

create-certificate-chain:
	# Export intermediate certificate to pem format
	keytool -keystore keystore.jks \
	    -storepass $(PASSWORD) \
	    -alias $(INTERNAME) -exportcert -rfc > $(INTERNAME).pem
	# Export ca certificate to pem format
	keytool -keystore keystore.jks \
    	    -storepass $(PASSWORD) \
    	    -alias ca -exportcert -rfc > ca.pem
    # Create chain
	cat ca.pem $(INTERNAME).pem > ca-chain.pem

add-client:
	# Generate client certificate
	keytool -genkey -alias $(CLIENTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_CLIENT) \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Generate a host certificate signing request
	keytool -certreq -alias $(CLIENTNAME) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(CLIENTNAME).csr" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Generate signed certificate with the intermediate certificate
	keytool -gencert -alias $(INTERNAME) \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(CLIENTNAME).csr" -outfile "$(CLIENTNAME).crt" -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Import signed certificate into the truststore
	keytool -import -trustcacerts -alias $(CLIENTNAME) \
	    -file "$(CLIENTNAME).crt" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	# Export private certificate for importing into a browser
	keytool -importkeystore -srcalias $(CLIENTNAME) \
	    -srckeystore $(KEYSTORE) -srcstorepass $(PASSWORD) \
	    -destkeystore "$(CLIENTNAME).p12" -deststorepass $(PASSWORD) \
	    -deststoretype PKCS12
	# Export private key in PEM format
	openssl pkcs12 -passin pass:$(PASSWORD) -in "$(CLIENTNAME).p12" \
	    -out $(CLIENTNAME).key.pem -nocerts -nodes
	# Export public certificate in PEM format
	openssl pkcs12 -passin pass:$(PASSWORD) -in "$(CLIENTNAME).p12" \
    	    -out "$(CLIENTNAME).crt.pem" -clcerts -nokeys


clean:
	# Remove generated artifacts
	find . \( -name "$(CLIENTNAME)*" -o -name "$(HOSTNAME)*" -o -name "$(KEYSTORE)" -o -name "$(TRUSTSTORE)" -o -name ca.crt \) -type f -exec rm -f {} \;
