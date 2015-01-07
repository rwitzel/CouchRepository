package com.github.rwitzel.couchrepository.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

/**
 * The primary key of this document is a composed object: {@link ExoticId}.
 * 
 * @author rwitzel
 */
@JsonInclude(Include.NON_NULL)
public class Exotic {

	/**
	 * CouchDB-specific property that allows us to distinct exotic documents from other documents.
	 */
	@JsonProperty("type")
	private String type = "exotic";

	/**
	 * The ID for CouchDB. Computed from {@link #id}.
	 */
	@SerializedName("_id")
	@JsonProperty("_id")
	private String computedKey;

	/**
	 * The domain-specific ID.
	 */
	@JsonProperty("internalId")
	private ExoticId id;

	/**
	 * The revision property for CouchDB.
	 */
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String version;

	private String data;

	public Exotic() {
		super();
	}

	public Exotic(ExoticId id) {
		super();
		this.id = id;
		this.computedKey = id.toCouchId();
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ExoticId getId() {
		return id;
	}
	
	public void setId(ExoticId id) {
		this.id = id;
	}

	public String getComputedKey() {
		return computedKey;
	}
	
	public void setComputedKey(String computedKey) {
		this.computedKey = computedKey;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
