package br.com.el.chatbot.controller;

import br.com.el.chatbot.openAI.OpenAiApi;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndexController {
    private boolean isInConversation = false;
    private boolean isInFAQ = false;
    private boolean isFirstMessage = true;
    private boolean faqSelectionMade = false;

    @PostMapping("/bot")
    public String messageReceiver(@RequestParam("Body") String body) {
        String response = "";

        try {
            if (isFirstMessage) {
                response = "Olá, eu sou a Elô! A assistente virtual da E&L Produções de Software! " +
                        "Digite abaixo a opção com que eu posso te ajudar:\n " +
                        "1- FAQ\n " +
                        "2- Conversa";
                isFirstMessage = false;
            } else if (body.equals("1") && !isInFAQ) {
                isInConversation = false;
                isInFAQ = true;
                faqSelectionMade = false;
                response = "Por favor, escolha uma das seguintes opções para o FAQ:\n " +
                        "1- Como saber os horários de ônibus do município de Vitória?\n " +
                        "2- Como saber os itinerários dos ônibus de Vitória?\n " +
                        "3- Como saber o andamento de um processo aberto na Prefeitura de Vitória?\n " +
                        "4- Onde obtenho informações sobre os serviços ofertados pela Prefeitura de Vitória?\n " +
                        "Se deseja parar o FAQ, digite 'sair'\n ";
            } else if (body.equals("2") && !isInConversation) {
                isInConversation = true;
                isInFAQ = false;
                faqSelectionMade = false;
                response = "Você entrou no modo de conversa. Qual é a sua dúvida?";
            } else {
                if (isInConversation) {
                    response = OpenAiApi.completePrompt(body);
                } else if (isInFAQ) {
                    if (body.equalsIgnoreCase("sair")) {
                        isInFAQ = false;
                        faqSelectionMade = false;
                        response = "Você saiu do FAQ. Digite 1 para FAQ ou 2 para começar uma conversa.";
                    } else {
                        int option = Integer.parseInt(body);
                        switch (option) {
                            case 1:
                                response = "O usuário do transporte coletivo municipal pode consultar pela internet serviço que informa o horário previsto em que o ônibus passa no ponto. É o Ponto Vitória, disponível no endereço https://www.vitoria.es.gov.br/pontovitoria\n";
                                faqSelectionMade = true;
                                break;
                            case 2:
                                response = "A informação está disponível no portal, no endereço https://sistemas.vitoria.es.gov.br/redeiti/";
                                faqSelectionMade = true;
                                break;
                            case 3:
                                response = "Quando o processo é aberto, o cidadão recebe o número do protocolo. De posse desse número, é possível consultar o andamento do processo no endereço http://sistemas.vitoria.es.gov.br/protocolo/\n";
                                faqSelectionMade = true;
                                break;
                            case 4:
                                response = "Essas informações estão disponíveis no Guia de Serviços, sistema de consulta on-line contendo descrição do serviço, público-alvo, atividades, dias e horários de atendimento, endereço e telefone. Endereço: http://sistemas.vitoria.es.gov.br/guiadeservicos";
                                faqSelectionMade = true;
                                break;
                            default:
                                if (!faqSelectionMade) {
                                    response = "Desculpe, não consegui entender. Por favor, escolha uma das opções de FAQ ou digite 'sair' para sair do FAQ.";
                                }
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = "Erro ao processar a mensagem.";
        }

        return response;
    }
}
