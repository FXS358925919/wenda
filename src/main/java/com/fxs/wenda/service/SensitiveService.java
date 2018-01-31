package com.fxs.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private static final String DEFAULT_REPLACEMENT = "***";
    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String lineText;
            while ((lineText = reader.readLine()) != null) {
                addWord(lineText.trim());
            }
            reader.close();
        }catch(Exception e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    private class TrieNode{
        private boolean end = false;
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key, node);
        }

        public TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        public boolean isKeyWordEnd(){
            return end;
        }

        public void setKeyWordEnd(boolean end) {
            this.end = end;
        }
    }


    private TrieNode rootNode = new TrieNode();



    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }
    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        // 循环每个字节
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { // 没初始化
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 关键词结束， 设置结束标志
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0; // 回滚数
        int position = 0; // 当前比较的位置

        while (position < text.length()) {
            char c = text.charAt(position);
            // 空格直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词， 从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }

        result.append(text.substring(begin));

        return result.toString();
    }
    public static void main(String[] args){
        SensitiveService service = new SensitiveService();
        service.addWord("色情");
        System.out.println(service.filter("你*好色*情"));
    }
}
