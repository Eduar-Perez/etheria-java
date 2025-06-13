package com.periferia.etheria.service;

public interface AgentIAClientService {

	public String sendQuestionToAgent(String questiong, String model, String agent, byte[] fileBase64);

}
