package org.b3mn.poem.util;

public class ExportInfo {

		protected String uri;
		protected String formatName;
		protected String iconUrl;
		
		public String getUri() {
			return uri;
		}

		public String getFormatName() {
			return formatName;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public ExportInfo(ExportHandler annotation) {
			this.uri = annotation.uri();
			this.iconUrl = annotation.iconUrl();
			this.formatName = annotation.formatName();
		}
}
