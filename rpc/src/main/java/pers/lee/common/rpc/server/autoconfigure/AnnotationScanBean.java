package pers.lee.common.rpc.server.autoconfigure;

import pers.lee.common.rpc.ci.management.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.ClassUtils;

import java.io.IOException;

public class AnnotationScanBean {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationScanBean.class);

	private static final String RESOURCE_PATTERN = "/**/*.class";

	private static final TypeFilter[] ENTITY_TYPE_FILTERS = new TypeFilter[] { new AnnotationTypeFilter(Document.class, false) };

	private String[] annotationPackages;
	private Class<?>[] annotationClasses;
	private EntityContext entityContext;
	private final ResourcePatternResolver resourcePatternResolver;

	public AnnotationScanBean() {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(new PathMatchingResourcePatternResolver());
	}

	public void setAnnotationPackages(String... annotationPackages) {
		for (String pkg : annotationPackages) {
			try {
				String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
				Resource[] resources = this.resourcePatternResolver.getResources(pattern);
				MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader reader = readerFactory.getMetadataReader(resource);
						String className = reader.getClassMetadata().getClassName();
						if (matchesFilter(reader, readerFactory)) {
							Class<?> clazz = this.resourcePatternResolver.getClassLoader().loadClass(className);
							entityContext.addPersistentEntity(clazz);
						}
					}
				}
			} catch (Exception e) {
				logger.error("Annotation scan package[{}] error", pkg, e);
			}
		}
	}

	public void setAnnotationClasses(Class<?>... annotationClasses) {
		for (Class<?> clazz : annotationClasses) {
			try {
				entityContext.addPersistentEntity(clazz);
			} catch (Exception e) {
				logger.error("Annotation scan class[{}] error", clazz, e);
			}
		}
	}

	private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
		for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
			if (filter.match(reader, readerFactory)) {
				return true;
			}
		}
		return false;
	}

	public Class<?>[] getAnnotationClasses() {
		return annotationClasses;
	}

	public String[] getAnnotationPackages() {
		return annotationPackages;
	}

	@Required
	public void setEntityContext(EntityContext entityContext) {
		this.entityContext = entityContext;
	}

}
