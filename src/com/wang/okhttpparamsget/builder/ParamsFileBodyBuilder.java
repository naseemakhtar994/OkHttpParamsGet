package com.wang.okhttpparamsget.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/3/7.
 */
public class ParamsFileBodyBuilder extends BaseBuilder {

    public ParamsFileBodyBuilder() {
        super("getBody", "builder");
    }

    @Override
    protected String getMethodType() {
        return "MultipartBody.Builder ";
    }

    @Override
    protected String getValueType() {
        return "MultipartBody";
    }

    private String getRequestBody() {
        return "RequestBody";
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("okhttp3.MultipartBody");
        imports.add("okhttp3.RequestBody");
        imports.add("okhttp3.MediaType");
        return imports;
    }

    @Override
    protected String buildMethod(PsiClass psiClass, boolean isOverride, boolean needAll) {
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(getMethodType()).append(mMethodName).append("(){");
        PsiField[] fields;
        if (isOverride && !needAll) {
            sb.append(getMethodType()).append(mFieldName).append("=super.").append(mMethodName).append("();");
            fields = psiClass.getFields();
        } else {
            sb.append(getMethodType()).append(mFieldName).append("=new MultipartBody.Builder().setType(MultipartBody.FORM);");
            fields = psiClass.getAllFields();
        }
        for (PsiField field : fields) {
            PsiModifierList modifiers = field.getModifierList();
            if (!findIgnore(modifiers)) {
                if (findPostFiles(modifiers)) {
                    sb.append("if (").append(field.getName()).append("!=null&&").append(field.getName()).append(".size()>0){");
                    sb.append("for (FileInput file : ").append(field.getName()).append(") {");
                    sb.append(mFieldName).append(".addFormDataPart(file.key, file.filename, ")
                            .append(getRequestBody()).append(".create(MediaType.parse(guessMimeType(file.filename)), file.file));}}");
                } else if (findPostFile(modifiers)) {
                    sb.append("if (").append(field.getName()).append("!=null){");
                    sb.append(mFieldName).append(".addFormDataPart(").append(field.getName()).append(".key,")
                            .append(field.getName()).append(".filename,").append(getRequestBody()).append(".create(MediaType.parse(guessMimeType(")
                            .append(field.getName()).append(".filename)),").append(field.getName()).append(".file));}");
                } else {
                    sb.append(mFieldName).append(".addFormDataPart(\"").append(field.getName()).append("\", String.valueOf(").append(field.getName()).append("));");
                }
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }
}
