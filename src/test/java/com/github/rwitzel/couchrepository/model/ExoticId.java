package com.github.rwitzel.couchrepository.model;

import java.io.Serializable;

/**
 * The primary key for {@link Exotic}.
 * 
 * @author rwitzel
 */
public class ExoticId implements Serializable {

	private static final long serialVersionUID = -2640126433764396121L;

	private Integer idPart1;

    private String idPart2;

    private Boolean idPart3;

    public ExoticId() {
        super();
    }

    public ExoticId(String couchId) {
        super();
        
        String[] parts = couchId.split("_");
        this.idPart1 = Integer.parseInt(parts[0]);
        this.idPart2 = parts[1];
        this.idPart3 = Boolean.parseBoolean(parts[2]);
    }

    public ExoticId(Integer idPart1, String idPart2, Boolean idPart3) {
        super();
        this.idPart1 = idPart1;
        this.idPart2 = idPart2;
        this.idPart3 = idPart3;
    }
    
    public Integer getIdPart1() {
		return idPart1;
	}

	public String getIdPart2() {
		return idPart2;
	}

	public Boolean getIdPart3() {
		return idPart3;
	}

	public String toCouchId() {
        return toCouchId(idPart1, idPart2, idPart3);
    }

    public static String toCouchId(Integer idPart1, String idPart2, Boolean idPart3) {
        return idPart1 + "_" + idPart2 + "_" + idPart3;
    }
}
