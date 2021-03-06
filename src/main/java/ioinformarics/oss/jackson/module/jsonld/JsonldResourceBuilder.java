package ioinformarics.oss.jackson.module.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldLink;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldProperty;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alexander De Leon
 */
public class JsonldResourceBuilder<T> {

    private String _context;
    private String _type;
    private String _id;
    private Function<T, String> _idSupplier;

    JsonldResourceBuilder(){
    }

    public JsonldResourceBuilder<T> context(String context){
        this._context = context;
        return this;
    }

    public JsonldResourceBuilder<T> type(String type){
        this._type = type;
        return this;
    }

    public JsonldResourceBuilder<T> id(String id){
        this._id = id;
        return this;
    }

    public JsonldResourceBuilder<T> id(Function<T, String> idSupplier){
        this._idSupplier = idSupplier;
        return this;
    }

    public JsonldResource build(T scopedObj) {
        if(scopedObj == null){
            return null;
        }
        if(Map.class.isAssignableFrom(scopedObj.getClass())){
            return new MapJsonldResource((Map)scopedObj, getContext(scopedObj).orElse(null), getType(scopedObj), getId(scopedObj));
        }
        return new BeanJsonldResource(scopedObj,  getContext(scopedObj).orElse(null), getType(scopedObj), getId(scopedObj));
    }

    protected Optional<JsonNode> getContext(T scopedObj) {
        return JsonldContextFactory.multiContext(Optional.ofNullable(_context), JsonldContextFactory.fromAnnotations(scopedObj));
    }

    protected String getId(T scopedObj){
        return Optional.ofNullable(_id).orElse(Optional.ofNullable(_idSupplier).map(f -> f.apply(scopedObj)).orElse(null));
    }

    protected String getType(T scopedObj) {
        return Optional.ofNullable(_type).orElse(dynamicTypeLookup(scopedObj.getClass()));
    }

    static String dynamicTypeLookup(Class<?> objType){
        JsonldType type = objType.getAnnotation(JsonldType.class);
        return type == null? null : type.value();
    }

}
